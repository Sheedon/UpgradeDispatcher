package org.sheedon.upgradelibrary.shareUtils;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Apk工具
 */
public class ApkUtils {
    private static final String TAG = ApkUtils.class.getSimpleName();

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
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
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本名称信息
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {

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

    // 开始安装,兼容8.0安装位置来源的权限
    // 1 成功
    // 0 失败
    // -1 没有权限
    public static int installApk(Context context, File downloadFile) {
        if (install(downloadFile)) {
            return 1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //是否有安装位置来源的权限
            boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {
                return installRealApk(context, downloadFile) ? 1 : 0;
            } else {
                return -1;
            }
        } else {
            return installRealApk(context, downloadFile) ? 1 : 0;
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

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     * <p>
     * 安装成功返回true，安装失败返回false。
     */
    private static boolean install(File file) {

        boolean result = false;

        DataOutputStream dataOutputStream = null;

        BufferedReader errorStream = null;

        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + file.getPath() + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
//            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException ignored) {
            }

        }

        return result;

    }

    /*
     * 启动一个app
     */
    public static void startAPP(Context context, String appPackageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void sendBroadcast(Context context, String packageName) {
        Intent callback = new Intent("org.sheedonsun.apk.message");
        callback.addCategory("receiverMessage");
        callback.putExtra("packageName", packageName);
        context.sendBroadcast(callback);
    }

    /**
     * apk是否安装
     */
    public static boolean isAppInstalled(String uri, Context context) {

        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;

    }

    /**
     * 复制文件到SD卡
     *
     * @param context
     * @param fileName 复制的文件名
     * @return
     */
    public static File copyAssetsFile(Context context, String fileName) {
        return copyAssetsFile(context, fileName, Environment.getExternalStorageDirectory() + "/apk");
    }

    public static File copyAssetsFile(Context context, String fileName, String outFile) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File file = new File(outFile);
            if (!file.exists()) {
                file.mkdirs();
            }
            File mFile = new File(file, fileName);
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] mbyte = new byte[1024];
            int i = 0;
            while ((i = mInputStream.read(mbyte)) > 0) {
                mFileOutputStream.write(mbyte, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();

            return mFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    //通过ActivityManager我们可以获得系统里正在运行的activities
    //包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
    public static boolean isRunning(Context context, String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 10 * 60 * 1000, now);

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {

                    for (UsageStats stat : stats) {
                        if (stat.getPackageName().equals(packageName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
