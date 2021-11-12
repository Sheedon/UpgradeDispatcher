package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.listener.UpgradeListener;

/**
 * 通知窗体管理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/26 1:21 下午
 */
public class DefaultNotifyManager extends UpgradeListener.UpgradeNotifyFactory {


    @Override
    public UpgradeListener createWindow(Context context) {
        return new NotificationWindow(context);
    }


    /**
     * 通知窗体
     */
    private static class NotificationWindow implements UpgradeListener {

        private final NotificationWrapper utils;

        public NotificationWindow(Context context) {
            this.utils = new NotificationWrapper(context);
        }


        @Override
        public void onUpgradeFailure(int code, String message) {
            utils.showNotification(message);
        }

        @Override
        public void onProgress(int progress) {
            utils.showNotification(progress);
        }

        @Override
        public void onStartDownload() {
        }

        @Override
        public void onDownloadSuccess() {

        }
    }
}
