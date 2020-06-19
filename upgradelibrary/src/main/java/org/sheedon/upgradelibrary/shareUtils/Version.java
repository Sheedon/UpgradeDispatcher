package org.sheedon.upgradelibrary.shareUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 版本信息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/14 9:01
 */
public class Version {

    private static final String KEY_UPGRADE_VERSION = "KEY_UPGRADE_VERSION";
    private static final String KEY_UPGRADE_TIME = "KEY_UPGRADE_TIME";


    // 升级版本
    private static int upgradeVersion;
    // 升级时间
    private static long upgradeTime;

    /**
     * 存储数据到XML文件，持久化
     */
    private static void save(Context context) {
        // 获取数据持久化的SP
        SharedPreferences sp = context.getSharedPreferences(Version.class.getName(),
                Context.MODE_PRIVATE);
        // 存储数据
        sp.edit()
                .putInt(KEY_UPGRADE_VERSION, upgradeVersion)
                .putLong(KEY_UPGRADE_TIME, upgradeTime)
                .apply();
    }

    /**
     * 进行数据加载
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Version.class.getName(),
                Context.MODE_PRIVATE);
        upgradeVersion = sp.getInt(KEY_UPGRADE_VERSION, 0);
        upgradeTime = sp.getLong(KEY_UPGRADE_TIME, 0);
    }


    /**
     * 保存升级时间
     *
     * @param context     上下文
     * @param upgradeTime 更新时间
     */
    public static void saveUpgradeTime(Context context, long upgradeTime) {
        Version.upgradeTime = upgradeTime;
        Version.save(context);
    }

    /**
     * 保存升级版本
     *
     * @param context        上下文
     * @param upgradeVersion 升级版本
     */
    public static void saveUpgradeVersion(Context context, int upgradeVersion) {
        Version.upgradeVersion = upgradeVersion;
        Version.save(context);
    }

    public static int getUpgradeVersion(Context context) {
        if (upgradeVersion == 0) {
            load(context);
        }
        return upgradeVersion;
    }

    public static long getUpgradeTime(Context context) {
        if (upgradeTime == 0) {
            load(context);
        }
        return upgradeTime;
    }
}
