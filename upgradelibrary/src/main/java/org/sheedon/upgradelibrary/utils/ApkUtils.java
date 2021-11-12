package org.sheedon.upgradelibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import org.sheedon.upgradelibrary.UpgradeConstants;

import java.io.File;

/**
 * APK 工具类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 1:35 下午
 */
public class ApkUtils {

    private static final String SUFFIX = "APK";

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序包名
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取包名最后一个节点的名称
     * 例如 com.abc.xxx
     * 拿到的就是xxx
     *
     * @param context 上下文
     * @return 包名最后一个节点
     */
    public static String getPackageLastName(Context context) {
        String packageName = ApkUtils.getPackageName(context);
        if (packageName != null && !packageName.isEmpty()) {
            String[] split = packageName.split("\\.");
            return split[split.length - 1];
        }

        return "default";
    }

    /**
     * 获取应用程序版本名称信息
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException ignored) {

        }
        return null;
    }

    /**
     * @return 当前程序的版本号
     */
    public static int getVersionCode(Context context) {
        int version;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            version = 0;
        }
        return version;
    }

    public static PackageInfo getApkPackageInfo(Context context, File file) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
    }

    /**
     * 是否是Apk文件
     *
     * @param file       文件
     * @param targetName 目标名称
     * @return 是否是Apk文件
     */
    public static boolean isApkFile(File file, String targetName) {
        if (file == null) {
            return false;
        }
        String fileName = file.getName();
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        targetName = targetName + UpgradeConstants.SUFFIX_DOT;
        return fileName.equalsIgnoreCase(targetName);
    }

    /**
     * 获取目标Apk是否完整并可以正常安装
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return 是否完整
     */
    public static boolean isIntactApk(Context context, String filePath) {
        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    // 开始安装,兼容8.0安装位置来源的权限
    // 1 成功
    // 0 失败
    // -1 没有权限
    public static boolean installApk(Context context, File downloadFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //是否有安装位置来源的权限
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {
                return installRealApk(context, downloadFile);
            } else {
                return false;
            }
        } else {
            return installRealApk(context, downloadFile);
        }
    }

    private static boolean installRealApk(Context context, File downloadFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PackageInfo apkPackageInfo = getApkPackageInfo(context, downloadFile);
            if (apkPackageInfo == null)
                return false;

            try {
                Uri apkUri = FileProvider.getUriForFile(context, apkPackageInfo.packageName + ".fileprovider", downloadFile);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(downloadFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
        return true;

    }
}
