package org.sheedon.upgradelibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;

/**
 * apk安装广播监听
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/12 11:08
 */
public class InstallApkReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            // 应用安装
            // 获取应用包名，和要监听的应用包名做对比
            String packName = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : "";

            if (packName.equals("")) {
                return;
            }

            checkStartApp(context, packName);
            Log.v("SXD", "ACTION_PACKAGE_ADDED" + packName);
        }
    }

    // 核实包名并且打开App
    private void checkStartApp(Context context, String packName) {
        if (packName.equals(ShareConstants.WAKE_APP_PACKAGE)) {
            ApkUtils.startAPP(context, packName);
        }
    }
}
