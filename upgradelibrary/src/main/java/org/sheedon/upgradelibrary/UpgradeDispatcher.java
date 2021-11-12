package org.sheedon.upgradelibrary;

import android.content.Context;

import org.sheedon.upgradelibrary.listener.DispatchListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallerManagerCenter;

import java.io.File;

/**
 * 更新调度者
 * 1。「下载模块」、「升级模块」附加调度监听器
 * 2。核实权限，权限有效，则走下一步，否则结束更新
 * 3。核实本地版本，并且下载apk，下载成功，则执行下一步，否则结束更新，提示下载失败
 * 4。安装Apk，安装失败，则提示，否则安装成功
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 11:08 上午
 */
class UpgradeDispatcher implements DispatchListener {

    // 下载模块
    private final DownloadManagerCenter downloader;
    // 安装模块
    private final InstallerManagerCenter installer;
    private final Context context;
    // 升级监听器
    private UpgradeListener listener;

    // 软件版本名称
    private String versionName;
    // apk 路径
    private String apkUrl;


    UpgradeDispatcher(Context context,
                      DownloadManagerCenter downloader,
                      InstallerManagerCenter installer) {
        this.context = context;
        this.downloader = downloader;
        this.installer = installer;
    }

    /**
     * 升级App
     *
     * @param versionName 版本名称
     * @param apkUrl      apk 路径
     * @param listener    监听器
     */
    void upgradeApp(Context context, String versionName, String apkUrl, UpgradeListener listener) {
        if (downloader.isRunning()) {
            return;
        }

        this.versionName = versionName;
        this.apkUrl = apkUrl;
        this.listener = listener;

        // 附加监听器
        downloader.attachListener(this);
        installer.attachListener(this);

        downloader.checkPermission(context);

    }

    /**
     * 取消
     */
    void cancel() {
        if (downloader.isRunning()) {
            downloader.cancel();
        }
    }

    /**
     * 执行下一项
     */
    @Override
    public void doNext() {
        downloader.checkLocalApk(versionName);
        downloader.downloadApk(apkUrl);
    }

    @Override
    public void onStartTask() {
        if(listener != null){
            listener.onStartDownload();
        }
    }

    /**
     * 更新
     *
     * @param progress 进度
     */
    @Override
    public void onProgress(int progress) {
        if (listener != null) {
            listener.onProgress(progress);
        }
    }

    /**
     * 升级失败
     *
     * @param code    失败编码
     * @param message 错误描述
     */
    @Override
    public void onUpgradeFailure(int code, String message) {
        if (listener != null) {
            listener.onUpgradeFailure(code, message);
        }
    }

    /**
     * 下载成功，安装apk
     *
     * @param downloadFile 下载文件
     */
    @Override
    public void onDownloadCompleted(File downloadFile) {
        if(listener != null){
            listener.onDownloadSuccess();
        }

        boolean isSuccess = installer.install(downloadFile);

        if (!isSuccess && listener != null) {
            listener.onUpgradeFailure(UpgradeListener.TYPE_INSTALL_FAILURE,
                    context.getString(R.string.app_install_failure));
        }
    }
}
