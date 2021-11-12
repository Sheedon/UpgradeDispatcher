package org.sheedon.upgradelibrary.listener;

import android.content.Context;

/**
 * 更新App监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 11:39 上午
 */
public interface UpgradeListener {

    int TYPE_PERMISSION = 1;// 授予权限失败
    int TYPE_DOWNLOAD_FAILURE = 2;// 下载失败
    int TYPE_INSTALL_FAILURE = 3;//  安装失败
    int TYPE_ERROR = 4;//  处理失败

    void onUpgradeFailure(int code, String message);

    void onProgress(int progress);

    void onStartDownload();

    void onDownloadSuccess();

    abstract class UpgradeNotifyFactory{
        // 创建展示窗体
        public UpgradeListener createWindow(Context context){
            throw new RuntimeException("please rewrite the method");
        }
    }
}
