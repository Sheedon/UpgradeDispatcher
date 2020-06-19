package org.sheedon.upgradelibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.sheedon.upgradelibrary.UpgradeInstaller;

/**
 * 通知消息
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/12 14:49
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String callback = intent.getStringExtra("callback");
        if (TextUtils.isEmpty(callback)) {
            return;
        }

        Log.v("SXD", "callback" + callback);

        handleCallback(context, callback);
    }

    // 处理消息
    private void handleCallback(Context context, String callback) {
        switch (callback) {
            case "opening":
                // 唤醒App已打开
                UpgradeInstaller.noticeWakeOpened(context);
                break;
            case "received":
                // 已接收
                UpgradeInstaller.noticeWakeReceived(context);
                break;
        }

    }
}
