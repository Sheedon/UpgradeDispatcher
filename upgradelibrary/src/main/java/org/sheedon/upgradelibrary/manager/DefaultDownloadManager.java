package org.sheedon.upgradelibrary.manager;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.liulishuo.okdownload.DownloadTask;

import org.sheedon.upgradelibrary.other.DownloadHandle;
import org.sheedon.upgradelibrary.other.DownloadListener;
import org.sheedon.upgradelibrary.other.UpgradeTask;
import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;
import org.sheedon.upgradelibrary.shareUtils.UpgradeStatusException;
import org.sheedon.upgradelibrary.shareUtils.Version;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 下载管理类
 * 初始化操作：
 * 1.创建默认apk下载地址父级目录
 * 2.核实当前版本和更新版本，文件夹下apk版本内容，删除无用apk包。
 * <p>
 * 更新操作
 * 1.核实本地下载路径，核实更新数据集
 * 2.下载apk
 * 3.通知下载进度（开始下载，下载量，下载失败，下载完成，重试次数）
 * 4.下载完成，通知下载流程结束
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 23:32
 */
public class DefaultDownloadManager implements DownloadManagerCenter, DownloadListener {

    private boolean setUpping = false;
    private File downloadFolder;

    private int upgradeVersion = 0;
    private int currentVersion = 0;

    private DownloadHandle downloadHandle;

    private boolean running = false;

    private static final Object lock = new Object();
    private ObservableEmitter<Integer> downloadEmitter;

    @Override
    public ObservableSource<Integer>[] setUp(Context context) {
        upgradeVersion = Version.getUpgradeVersion(context);
        currentVersion = ApkUtils.getVersionCode(context);

        if (setUpping)
            return null;

        return new ObservableSource[]{createParentFile(context)};
    }

    // 创建默认apk下载地址父级目录
    // 核实当前版本和更新版本，文件夹下apk版本内容，删除无用apk包。
    private ObservableSource<Integer> createParentFile(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                if (setUpping)
                    return;

                setUpping = true;


                if (downloadFolder == null) {
                    downloadFolder = new File(ShareConstants.UPDATE_APP_PATH);
                }

                if (!downloadFolder.exists()) {
                    downloadFolder.mkdirs();
                }

                if (downloadFolder == null) {
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_INIT_FILE_CREATION_FAILED));
                    return;
                }


                File[] files = downloadFolder.listFiles();

                if (files == null) {
                    setUpping = false;
                    emitter.onNext(ShareConstants.STATUS_INIT_DOWNLOAD_COMPLETE);
                    return;
                }

                for (File file : files) {
                    clearFile(context, file);
                }
                emitter.onNext(ShareConstants.STATUS_INIT_DOWNLOAD_COMPLETE);
                setUpping = false;
            }
        });
    }


    // 清理apk文件
    private void clearFile(Context context, File file) {
        if (file.getName().endsWith(".apk")) {
            PackageInfo apkPackageInfo = ApkUtils.getApkPackageInfo(context, file);
            if (apkPackageInfo == null)
                return;

            if (apkPackageInfo.versionCode <= currentVersion) {
                file.delete();
            }
        }
    }

    @Override
    public ObservableSource<Integer>[] upgradeDispatch(Context context, UpgradeTask model) {
        if (running) {
            return null;
        }

        return new ObservableSource[]{checkDownloadTask(model), downloadTask(model)};
    }

    // 核实本地下载路径，核实更新数据集
    private ObservableSource<Integer> checkDownloadTask(final UpgradeTask model) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                if (running)
                    return;

                running = true;

                if (model == null || model.getNetVersionModel() == null) {
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_PARAMETER_ERROR));
                    return;
                }

                upgradeVersion = model.getNetVersionModel().getVersion();
                if (upgradeVersion <= currentVersion) {
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_GREATER_THAN_CURRENT_VERSION));
                    return;
                }

                String path = model.getNetVersionModel().getPath();
                if (path == null || path.isEmpty()) {
                    emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_PARAMETER_ERROR));
                    return;
                }

                downloadHandle = new DownloadHandle(model.getReCount());

            }
        });
    }

    // 下载任务
    private ObservableSource<Integer> downloadTask(final UpgradeTask task) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
                if (!running)
                    return;

                downloadEmitter = emitter;
                synchronized (lock) {
                    try {
                        downloadHandle.downloadTask(task.getNetVersionModel().getPath(),
                                task.getParentFile(),
                                task.getFileName(), DefaultDownloadManager.this);

                        lock.wait();
                    } catch (InterruptedException ignored) {
                        emitter.onError(new UpgradeStatusException(ShareConstants.STATUS_UPGRADE_DOWNLOAD_FAIL));
                    } finally {
                        running = false;
                        downloadEmitter = null;
                    }
                }

            }
        });
    }

    @Override
    public void start(DownloadTask task) {
        if (downloadEmitter != null) {
            downloadEmitter.onNext(ShareConstants.STATUS_UPGRADE_DOWNLOAD_START);
        }
    }

    @Override
    public void progress(int progress) {
        if (downloadEmitter != null) {
            downloadEmitter.onNext(progress);
        }
    }

    @Override
    public void completed() {
        if (downloadEmitter != null) {
            downloadEmitter.onNext(ShareConstants.STATUS_UPGRADE_DOWNLOAD_COMPLETE);
        }

        synchronized (lock) {
            lock.notifyAll();
        }

        if (downloadHandle != null) {
            downloadHandle.destroy();
        }
        downloadHandle = null;
    }

    @Override
    public void error(String message) {
        if (downloadEmitter != null) {
            downloadEmitter.onError(new UpgradeStatusException(message));
        }

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
