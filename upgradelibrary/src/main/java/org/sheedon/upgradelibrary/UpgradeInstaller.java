package org.sheedon.upgradelibrary;

import android.app.Application;
import android.content.Context;

import org.sheedon.upgradelibrary.listener.InstallListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.model.NetVersionModel;
import org.sheedon.upgradelibrary.other.UpgradeTask;

/**
 * 升级安装程序
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 22:20
 */
public class UpgradeInstaller {
    private static final String TAG = "Upgrade.UpgradeInstaller";

    public static Upgrade setUp(Application application, InstallListener listener) {
        Upgrade upgrade = new Upgrade.Builder(application).bindInstallListener(listener).build();
        upgrade.setUp();
        return upgrade;
    }

    /**
     * 安装的升级包
     *
     * @param context
     * @param model
     */
    public static void onReceiveUpgradeInfo(Context context, UpgradeTask model, UpgradeListener listener) {
        Upgrade.with(context).onUpgradeReceived(model, listener);
    }

    public static void noticeWakeOpened(Context context) {
        if (Upgrade.isUpgradeInstalled())
            Upgrade.with(context).noticeWakeOpened();
    }

    public static void cancel(Context context) {
        if (Upgrade.isUpgradeInstalled())
            Upgrade.with(context).cancel();
    }

    public static void noticeWakeReceived(Context context) {
        if (Upgrade.isUpgradeInstalled())
            Upgrade.with(context).noticeWakeReceived();
    }
}
