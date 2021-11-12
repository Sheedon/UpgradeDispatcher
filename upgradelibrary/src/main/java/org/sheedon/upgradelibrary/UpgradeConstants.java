package org.sheedon.upgradelibrary;

import android.os.Environment;

/**
 * 升级常量
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 1:31 下午
 */
public interface UpgradeConstants {

    String APK_NAME = "default.apk";
    String SUFFIX_DOT = ".apk";

    // 根目录
    String ROOT_DRI = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 更新App下载目录
    String UPDATE_APP_PATH = ROOT_DRI + "/BlueLight/";
}
