package org.sheedon.upgradelibrary.model;

import android.content.Context;

import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.Version;

/**
 * 当前保存的记录更新版本号
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 22:42
 */
public class UpgradeVersionModel {

    // 更新版本号
    private int upgradeVersion;
    // 当前版本号
    private int currentVersion;
    // 更新时间
    private long upgradeTime;

    public static UpgradeVersionModel build(int upgradeVersion, int currentVersion, long upgradeTime) {
        UpgradeVersionModel model = new UpgradeVersionModel();
        model.upgradeVersion = upgradeVersion;
        model.currentVersion = currentVersion;
        model.upgradeTime = upgradeTime;
        return model;
    }

    public static UpgradeVersionModel build(Context context) {
        UpgradeVersionModel model = new UpgradeVersionModel();
        model.upgradeVersion = Version.getUpgradeVersion(context);
        model.currentVersion = ApkUtils.getVersionCode(context);
        model.upgradeTime = Version.getUpgradeTime(context);
        return model;
    }

    public int getUpgradeVersion() {
        return upgradeVersion;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public long getUpgradeTime() {
        return upgradeTime;
    }

    public boolean isUpgradeSuccess() {
        return currentVersion >= upgradeVersion;
    }
}
