package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;
import org.sheedon.upgradelibrary.shareUtils.UpgradeStatusException;

import java.io.File;
import java.util.Arrays;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 唤醒模块管理类
 * 功能:
 * 初始化
 * 1.检测是否安装唤醒App
 * 存在 -> 完成
 * 不存在 -> 安装唤醒App
 * 2.查看指定文件夹下是否存在需要安装的Apk包
 * 存在 -> 删除 防止是old-apk或者其他同名apk
 * 3.将资源文件夹assets中的apk包导入到指定目录下
 * 4.安装apk
 * 5.安装完成 -> 发送通知告诉更新App
 * <p>
 * 更新调度
 * 1.核实唤醒App是否启用
 * 未启用 -> 开启唤醒App
 * 2.广播校验，接收到唤醒App的反馈，代表已有效
 * 3.升级第一步骤完成
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 23:16
 */
public class DefaultWakeManager implements WakeManagerCenter {

    private File wakeFolder;
    private static final Object lock = new Object();

    private boolean setUpping = false;

    @Override
    public ObservableSource<Integer>[] setUp(Context context) {
        if (setUpping)
            return null;
        return new ObservableSource[]{createCheckInstalled(context),
                createClearFile(), createExportAndInstallFile(context), createWaitNoticeToCheck()};

    }

    // 第一步：创建核实是否已安装【唤醒App】
    private ObservableSource<Integer> createCheckInstalled(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                setUpping = true;
                // 检测是否安装唤醒App
                if (ApkUtils.isAppInstalled(ShareConstants.WAKE_APP_PACKAGE, context)) {
                    startReceiverApp(context);
                    setUpping = false;
                    sendNext(emitter, ShareConstants.STATUS_INIT_WAKE_INSTALLED);
                }
            }
        });
    }

    // 第二步:查看指定文件夹下是否存在需要安装的Apk包
    private ObservableSource<Integer> createClearFile() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                // 代表已完成
                if (!setUpping)
                    return;

                if (wakeFolder == null) {
                    wakeFolder = new File(ShareConstants.WAKE_APP_PACKAGE);
                }

                if (!wakeFolder.exists()) {
                    wakeFolder.mkdirs();
                }

                if (wakeFolder == null) {
                    sendError(emitter, ShareConstants.STATUS_INIT_FILE_CREATION_FAILED);
                    return;
                }


                File[] files = wakeFolder.listFiles();

                if (files == null)
                    return;

                for (File file : files) {
                    file.delete();
                }
            }
        });
    }

    // 第三步+第四步:将资源文件夹assets中的apk包导入到指定目录下,并且安装
    private ObservableSource<Integer> createExportAndInstallFile(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                // 代表已完成
                if (!setUpping)
                    return;

                File file = ApkUtils.copyAssetsFile(context, ShareConstants.WAKE_APP_NAME, ShareConstants.WAKE_APP_PATH);

                if (file == null) {
                    // 通知导出失败
                    sendError(emitter, ShareConstants.STATUS_INIT_FILE_EXPORT_FAILED);
                    setUpping = false;
                    return;
                }

                // 安装
                int code = ApkUtils.installApk(context, file);
                if (code == 1) {
                    // 成功
                    sendNext(emitter, ShareConstants.STATUS_INIT_WAKE_INSTALLED);
                } else if (code == -1) {
                    // 没有权限
                    sendError(emitter, ShareConstants.STATUS_INIT_FILE_PERMISSION_DENIED);
                    setUpping = true;
                } else {
                    // 失败
                    sendError(emitter, ShareConstants.STATUS_INIT_INSTALLATION_FAILED);
                    setUpping = true;
                }
            }
        });
    }

    // 第五步：安装完成，打开【唤醒App】+反馈当前App广播，已完成app校验
    // 由 InstallApkReceiver 广播监听【唤醒App】安装
    // 由 MessageReceiver 广播监听打开反馈
    // 开始监听,设置超时处理15秒
    private ObservableSource<Integer> createWaitNoticeToCheck() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {


            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                // 代表已完成
                if (!setUpping)
                    return;

                synchronized (lock) {
                    try {
                        long startTime = System.currentTimeMillis();
                        lock.wait(15 * 1000);
                        long endTime = System.currentTimeMillis();
                        if (endTime - startTime >= 15 * 1000) {
                            sendError(emitter, ShareConstants.STATUS_INIT_NOTIFICATION_TIMEOUT);
                        } else {
                            sendNext(emitter, ShareConstants.STATUS_INIT_WAKE_COMPLETE);
                        }
                    } catch (InterruptedException ignored) {
                        sendNext(emitter, ShareConstants.STATUS_INIT_WAKE_COMPLETE);
                    } finally {
                        setUpping = false;
                    }
                }

            }
        });
    }

    // 更新App，【唤醒App】启动
    @Override
    public ObservableSource<Integer>[] upgradeDispatch(Context context) {
        if (setUpping) {
            return null;
        }

        ObservableSource[] sources = null;
        if (!ApkUtils.isAppInstalled(ShareConstants.WAKE_APP_PACKAGE, context)) {
            sources = setUp(context);
        }

        if (sources == null) {
            return new ObservableSource[]{createCheckRunning(context), createWaitNoticeToReceived()};
        }

        sources = Arrays.copyOf(sources, sources.length + 2);
        sources[sources.length - 2] = createCheckRunning(context);
        sources[sources.length - 1] = createWaitNoticeToReceived();
        return sources;
    }


    // 更新App
    // 第一步核实【唤醒App是否存在】
    private ObservableSource<Integer> createCheckRunning(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                boolean isRunning = ApkUtils.isRunning(context, ShareConstants.WAKE_APP_PACKAGE);
                if (!isRunning) {
                    ApkUtils.startAPP(context, ShareConstants.WAKE_APP_PACKAGE);

                    synchronized (lock) {
                        try {
                            long startTime = System.currentTimeMillis();
                            lock.wait(15 * 1000);
                            long endTime = System.currentTimeMillis();
                            if (endTime - startTime >= 15 * 1000) {
                                sendError(emitter, ShareConstants.STATUS_UPGRADE_NOTIFICATION_TIMEOUT);
                            } else {
                                ApkUtils.sendBroadcast(context, ApkUtils.getPackageName(context));
                            }
                        } catch (InterruptedException ignored) {
                            ApkUtils.sendBroadcast(context, ApkUtils.getPackageName(context));
                        }
                    }

                } else {
                    ApkUtils.sendBroadcast(context, ApkUtils.getPackageName(context));
                }
            }
        });
    }


    // 第二步：打开【唤醒App】+反馈当前App广播，已完成app校验
    // 由 MessageReceiver 广播监听打开反馈
    // 开始监听,设置超时处理15秒
    private ObservableSource<Integer> createWaitNoticeToReceived() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {

                synchronized (lock) {
                    try {
                        long startTime = System.currentTimeMillis();
                        lock.wait(15 * 1000);
                        long endTime = System.currentTimeMillis();

                        if (endTime - startTime >= 15 * 1000) {
                            sendError(emitter, ShareConstants.STATUS_UPGRADE_NOTIFICATION_TIMEOUT);
                        } else {
                            sendNext(emitter, ShareConstants.STATUS_UPGRADE_RECEIVED);
                        }
                    } catch (InterruptedException ignored) {
                        sendNext(emitter, ShareConstants.STATUS_UPGRADE_RECEIVED);
                    }
                }

            }
        });
    }

    @Override
    public void noticeWakeOpened() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void noticeWakeReceived() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }


    // 启动广播监听App
    private void startReceiverApp(Context context) {
        ApkUtils.startAPP(context, ShareConstants.WAKE_APP_PACKAGE);
    }

    private void sendNext(ObservableEmitter<Integer> emitter, int status) {
        if (!emitter.isDisposed())
            emitter.onNext(status);
    }

    private void sendError(ObservableEmitter<Integer> emitter, int status) {
        if (!emitter.isDisposed())
            emitter.onError(new UpgradeStatusException(status));
    }
}
