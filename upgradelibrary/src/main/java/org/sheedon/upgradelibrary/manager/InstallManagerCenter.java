package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.other.UpgradeTask;

import io.reactivex.rxjava3.core.ObservableSource;

/**
 * 安装管理中心接口
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/14 8:23
 */
public interface InstallManagerCenter {

    // 初始化
    ObservableSource<Integer>[] setUp(Context context);

    // 升级调度
    ObservableSource<Integer>[] upgradeDispatch(Context context, UpgradeTask model);

    // 通知已接收需要更新的Apk包名【唤醒App】
    void noticeWakeReceived();
}
