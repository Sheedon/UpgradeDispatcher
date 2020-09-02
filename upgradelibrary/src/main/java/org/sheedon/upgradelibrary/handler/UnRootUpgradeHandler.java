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
 * 无root的设备升级
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/9/2 10:33 AM
 */
public class UnRootUpgradeHandler implements HandleCenter {

    private DownloadManagerCenter downloadManager;
    private InstallManagerCenter installManager;


    @Override
    public void initConfig(Context context, WakeManagerCenter wakeManagerCenter, DownloadManagerCenter downloadManagerCenter, InstallManagerCenter installManagerCenter, Observer<Integer> currentObserver) {
        this.downloadManager = downloadManagerCenter;
        this.installManager = installManagerCenter;
    }

    @Override
    public void upgrade(Context context, UpgradeTask model, Observer<Integer> currentObserver) {

        ObservableSource<Integer>[] downloadSources = downloadManager.upgradeDispatch(context, model);
        if (downloadSources == null) {
            return;
        }

        ObservableSource<Integer>[] installSources = installManager.installDispatch(context, model);
        if (installSources == null) {
            return;
        }

        List<ObservableSource<Integer>> sources = new ArrayList<>();
        sources.addAll(Arrays.asList(downloadSources));
        sources.addAll(Arrays.asList(installSources));

        Observable.merge(sources)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(currentObserver);
    }

    @Override
    public void destroy() {
        downloadManager = null;
        installManager = null;

    }
}
