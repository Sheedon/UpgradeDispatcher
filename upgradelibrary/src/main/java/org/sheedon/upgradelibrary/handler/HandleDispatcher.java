package org.sheedon.upgradelibrary.handler;

import android.content.Context;

import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallManagerCenter;
import org.sheedon.upgradelibrary.manager.WakeManagerCenter;
import org.sheedon.upgradelibrary.other.UpgradeTask;
import org.sheedon.upgradelibrary.shareUtils.ApkUtils;

import io.reactivex.rxjava3.core.Observer;

/**
 * 调度器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/9/2 10:37 AM
 */
public class HandleDispatcher implements HandleCenter {

    private HandleCenter handler;

    public HandleDispatcher() {

        handler = ApkUtils.isRoot() ? new RootUpgradeHandler() : new UnRootUpgradeHandler();

    }


    @Override
    public void initConfig(Context context, WakeManagerCenter wakeManagerCenter, DownloadManagerCenter downloadManagerCenter, InstallManagerCenter installManagerCenter, Observer<Integer> currentObserver) {
        if (handler != null) {
            handler.initConfig(context, wakeManagerCenter, downloadManagerCenter, installManagerCenter, currentObserver);
        }
    }

    @Override
    public void upgrade(Context context, UpgradeTask model, Observer<Integer> currentObserver) {
        if (handler != null) {
            handler.upgrade(context, model, currentObserver);
        }
    }

    @Override
    public void destroy() {
        if (handler != null) {
            handler.destroy();
        }
    }
}
