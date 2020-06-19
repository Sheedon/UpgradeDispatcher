package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.other.UpgradeTask;

import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 下载管理中心
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/14 8:22
 */
public interface DownloadManagerCenter {

    // 初始化
    ObservableSource<Integer>[] setUp(Context context);

    // 升级调度
    ObservableSource<Integer>[] upgradeDispatch(Context context, UpgradeTask model);
}
