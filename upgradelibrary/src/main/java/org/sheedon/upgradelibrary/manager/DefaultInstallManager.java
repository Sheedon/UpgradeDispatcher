package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.other.UpgradeTask;
import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;
import org.sheedon.upgradelibrary.shareUtils.UpgradeStatusException;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 安装管理类
 * 初始化：
 * 注：必须在唤醒管理类执行完成后才能使用
 * 无额外初始化处理
 * <p>
 * 更新操作
 * 1.调查通知测试，广播传输，获取是否启动
 * 2.安装Apk，依次：静默更新 -> 系统更新
 * 2.1 静默更新（才需要【唤醒App】，用于App更新关闭后的重启）*只执行
 * 2.2 系统更新，一般不执行
 * 3.【唤醒App】监听调度启动当前App
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 23:32
 */
public class DefaultInstallManager implements InstallManagerCenter {

    private static final Object lock = new Object();

    @Override
    public ObservableSource<Integer>[] setUp(Context context) {
        return new ObservableSource[]{createDefault()};
    }

    private ObservableSource<Integer> createDefault() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                emitter.onNext(ShareConstants.STATUS_INIT_INSTALL_COMPLETE);
            }
        });
    }

    @Override
    public ObservableSource<Integer>[] upgradeDispatch(Context context, UpgradeTask model) {
        return new ObservableSource[]{createWaitNoticeToReceived(context), installApk(context, model)};
    }

    // 1.调查通知测试，广播传输，获取是否启动
    private ObservableSource<Integer> createWaitNoticeToReceived(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {

                synchronized (lock) {
                    try {
                        ApkUtils.sendBroadcast(context, ApkUtils.getPackageName(context));
                        long startTime = System.currentTimeMillis();
                        lock.wait(15 * 1000);
                        long endTime = System.currentTimeMillis();

                        if (emitter.isDisposed())
                            return;

                        if (endTime - startTime >= 15 * 1000) {
                            emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_NOTIFICATION_TIMEOUT));
                        } else {
                            emitter.onNext(ShareConstants.STATUS_UPGRADE_RECEIVED);
                        }
                    } catch (InterruptedException ignored) {
                        emitter.onNext(ShareConstants.STATUS_UPGRADE_RECEIVED);
                    }
                }

            }
        });
    }

    // 2.安装Apk，依次：静默更新 -> 系统更新
    private ObservableSource<Integer> installApk(final Context context, final UpgradeTask model) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {

                emitter.onNext(ShareConstants.STATUS_UPGRADE_INSTALL_START);
                int code = ApkUtils.installApk(context, new File(model.getParentFile(), model.getFileName()));

                if (code == 1) {
                    // 成功
                    emitter.onNext(ShareConstants.STATUS_UPGRADE_INSTALLED);
                } else if (code == -1) {
                    // 没有权限
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_INIT_FILE_PERMISSION_DENIED));
                } else {
                    // 失败
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_INSTALL_FAILED));
                }
            }
        });
    }


    @Override
    public void noticeWakeReceived() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
