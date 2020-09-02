package org.sheedon.upgradelibrary.handler;

import android.content.Context;

import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallManagerCenter;
import org.sheedon.upgradelibrary.manager.WakeManagerCenter;
import org.sheedon.upgradelibrary.other.UpgradeTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 已root的设备升级处理
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/9/2 10:33 AM
 */
public class RootUpgradeHandler implements HandleCenter {

    private WakeManagerCenter wakeManager;
    private DownloadManagerCenter downloadManager;
    private InstallManagerCenter installManager;


    @Override
    public void initConfig(Context context, WakeManagerCenter wakeManagerCenter, DownloadManagerCenter downloadManagerCenter,
                           InstallManagerCenter installManagerCenter, Observer<Integer> currentObserver) {

        this.wakeManager = wakeManagerCenter;
        this.downloadManager = downloadManagerCenter;
        this.installManager = installManagerCenter;

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
                .subscribe(currentObserver);

    }

    @Override
    public void upgrade(Context context, UpgradeTask model, Observer<Integer> currentObserver) {
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
                .subscribe(currentObserver);
    }


    @Override
    public void destroy() {
        wakeManager = null;
        downloadManager = null;
        installManager = null;
    }
}
