package org.sheedon.upgradelibrary.handler;

import android.content.Context;

import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallManagerCenter;
import org.sheedon.upgradelibrary.manager.WakeManagerCenter;
import org.sheedon.upgradelibrary.other.UpgradeTask;

import io.reactivex.rxjava3.core.Observer;

/**
 * 处理中心
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/9/2 10:29 AM
 */
public interface HandleCenter {

    void initConfig(Context context, WakeManagerCenter wakeManagerCenter, DownloadManagerCenter downloadManagerCenter,
                    InstallManagerCenter installManagerCenter, Observer<Integer> currentObserver);

    void upgrade(Context context, UpgradeTask model, Observer<Integer> currentObserver);

    void destroy();

}
