package org.sheedon.upgradelibrary;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.sheedon.upgradelibrary.listener.InstallListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.manager.DefaultDownloadManager;
import org.sheedon.upgradelibrary.manager.DefaultInstallManager;
import org.sheedon.upgradelibrary.manager.DefaultWakeManager;
import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallManagerCenter;
import org.sheedon.upgradelibrary.manager.WakeManagerCenter;
import org.sheedon.upgradelibrary.model.NetVersionModel;
import org.sheedon.upgradelibrary.model.UpgradeVersionModel;
import org.sheedon.upgradelibrary.other.UpgradeTask;
import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;
import org.sheedon.upgradelibrary.shareUtils.StatusUtils;
import org.sheedon.upgradelibrary.shareUtils.Version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * App升级核心单例类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 22:16
 */
public class Upgrade {
    private static final String TAG = "Upgrade.Upgrade";

    private static Upgrade sInstance;
    private static boolean sInstalled = false;

    final Context context;
    int status;
    final WakeManagerCenter wakeManager;
    final DownloadManagerCenter downloadManager;
    final InstallManagerCenter installManager;
    final InstallListener listener;

    private Observer<Integer> currentObserver;
    private Disposable disposable;
    private String msg;

    private UpgradeListener upgradeListener;


    public Upgrade(Context context, int status,
                   WakeManagerCenter wakeManager, DownloadManagerCenter downloadManager,
                   InstallManagerCenter installManager, InstallListener listener) {
        this.context = context;
        this.status = status;
        this.wakeManager = wakeManager;
        this.downloadManager = downloadManager;
        this.installManager = installManager;
        this.listener = listener;
    }

    private Observer createObserver() {
        if (currentObserver == null) {
            synchronized (Upgrade.class) {
                if (currentObserver == null) {
                    currentObserver = new Observer<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(Integer integer) {
                            status = integer;
                            Log.v("SXD", "" + integer);


                            if (upgradeListener != null) {
                                if (integer >= 0 && integer <= 100) {
                                    upgradeListener.onProgress(integer);
                                } else {
                                    upgradeListener.onUpgradeStatus(integer);
                                }
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            try {
                                String message = e.getMessage();
                                if (message == null)
                                    message = "";
                                status = Integer.parseInt(message);
                                if (upgradeListener != null)
                                    upgradeListener.onUpgradeError(StatusUtils.convertStatus(status));
                            } catch (NumberFormatException ignored) {
                                status = ShareConstants.STATUS_NORMAL;
                                msg = e.getMessage();
                                if (upgradeListener != null)
                                    upgradeListener.onUpgradeError(msg);
                            }
                            Log.v("SXD", "" + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    };
                }
            }
        }
        return currentObserver;
    }

    /**
     * 使用默认配置升级进行初始化
     *
     * @param context 我们将使用Application
     * @return the Upgrade object
     */
    public static Upgrade with(Context context) {
        if (!sInstalled) {
            throw new RuntimeException("you must install upgrade before get upgrade sInstance");
        }
        if (sInstance == null) {
            synchronized (Upgrade.class) {
                if (sInstance == null) {
                    sInstance = new Builder(context).build();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     */
    public void setUp() {
        sInstalled = true;

        if (listener != null) {
            listener.onResultCallback(UpgradeVersionModel.build(context));
        }

        ObservableSource<Integer>[] wakeSources = wakeManager.setUp(context);
        if (wakeSources == null)
            return;

        ObservableSource<Integer>[] downloadSources = downloadManager.setUp(context);
        if (downloadSources == null)
            return;

        ObservableSource<Integer>[] installSources = installManager.setUp(context);
        if (installSources == null)
            return;

        List<ObservableSource<Integer>> sources = new ArrayList<>();
        sources.addAll(Arrays.asList(wakeSources));
        sources.addAll(Arrays.asList(downloadSources));
        sources.addAll(Arrays.asList(installSources));


        Observable.merge(sources)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(createObserver());

    }


    public static boolean isUpgradeInstalled() {
        return sInstalled;
    }


    /**
     * 升级
     *
     * @param model 网络更新任务model
     */
    public void onUpgradeReceived(UpgradeTask model, UpgradeListener listener) {
        this.upgradeListener = listener;
        ObservableSource<Integer>[] wakeSources = wakeManager.upgradeDispatch(context);
        if (wakeSources == null) {
            return;
        }

        ObservableSource<Integer>[] downloadSources = downloadManager.upgradeDispatch(context, model);
        if (downloadSources == null) {
            return;
        }

        ObservableSource<Integer>[] installSources = installManager.upgradeDispatch(context, model);
        if (installSources == null) {
            return;
        }

        List<ObservableSource<Integer>> sources = new ArrayList<>();
        sources.addAll(Arrays.asList(wakeSources));
        sources.addAll(Arrays.asList(downloadSources));
        sources.addAll(Arrays.asList(installSources));

        Observable.merge(sources)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(createObserver());
    }

    /**
     * 通知已打开【唤醒App】
     */
    public void noticeWakeOpened() {
        if (wakeManager != null) {
            wakeManager.noticeWakeOpened();
        }
    }

    /**
     * 通知已接收需要更新的Apk包名【唤醒App】
     */
    public void noticeWakeReceived() {
        if (wakeManager != null) {
            wakeManager.noticeWakeReceived();
        }
        if (installManager != null) {
            installManager.noticeWakeReceived();
        }
    }

    public void cancel() {

        if(upgradeListener != null){
            upgradeListener.onUpgradeStatus(ShareConstants.STATUS_UPGRADE_CANCEL);
        }

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static class Builder {

        private final Context context;
        private InstallListener listener;

        private WakeManagerCenter wakeManager;
        private DownloadManagerCenter downloadManager;
        private InstallManagerCenter installManager;

        private int status = -999;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder wakeManager(WakeManagerCenter wakeManager) {
            if (wakeManager == null) {
                throw new RuntimeException("wakeManager must not be null.");
            }
            this.wakeManager = wakeManager;
            return this;
        }

        public Builder downloadManager(DownloadManagerCenter downloadManager) {
            if (downloadManager == null) {
                throw new RuntimeException("downloadManager must not be null.");
            }
            this.downloadManager = downloadManager;
            return this;
        }

        public Builder installManager(InstallManagerCenter installManager) {
            if (installManager == null) {
                throw new RuntimeException("installManager must not be null.");
            }
            this.installManager = installManager;
            return this;
        }

        public Builder bindInstallListener(InstallListener listener) {
            this.listener = listener;
            return this;
        }


        public Upgrade build() {
            if (status == -999) {
                status = ShareConstants.STATUS_NORMAL;
            }

            if (wakeManager == null) {
                wakeManager = new DefaultWakeManager();
            }

            if (downloadManager == null) {
                downloadManager = new DefaultDownloadManager();
            }

            if (installManager == null) {
                installManager = new DefaultInstallManager();
            }

            Version.load(context);

            return new Upgrade(context, status,
                    wakeManager, downloadManager, installManager,
                    listener);
        }
    }
}
