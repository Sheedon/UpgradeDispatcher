package org.sheedon.upgradelibrary.manager;


import static android.app.Notification.VISIBILITY_SECRET;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.sheedon.upgradelibrary.R;

/**
 * 通知包装类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 10:57 上午
 */
public class NotificationWrapper extends ContextWrapper {


    public static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default_Channel";
    private NotificationManager mManager;
    private NotificationChannel channel;
    private Notification notification;
    private int[] flags;

    private RemoteViews remoteViews;

    public NotificationWrapper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            channel = createNotificationChannel();
        }
    }

    /**
     * 获取通知渠道
     */
    @TargetApi(Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel() {
        //第一个参数：channel_id
        //第二个参数：channel_name
        //第三个参数：设置通知重要性级别
        //注意：该级别必须要在 NotificationChannel 的构造函数中指定，总共要五个级别；
        //范围是从 NotificationManager.IMPORTANCE_NONE(0) ~ NotificationManager.IMPORTANCE_HIGH(4)
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);
        channel.canBypassDnd();//是否绕过请勿打扰模式
        channel.enableLights(true);//是否在桌面icon右上角展示小红点
        channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
        channel.setLightColor(Color.RED);//闪关灯的灯光颜色
        channel.canShowBadge();//桌面launcher的消息角标
        //channel.enableVibration(false);//是否允许震动
        channel.getAudioAttributes();//获取系统通知响铃声音的配置
        channel.getGroup();//获取通知取到组
        channel.setBypassDnd(true);//设置可绕过 请勿打扰模式
        channel.setSound(null, null);
        //channel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
        channel.shouldShowLights();//是否会有灯光
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        getManager().createNotificationChannel(channel);
        return channel;
    }

    /**
     * 获取创建一个NotificationManager的对象
     *
     * @return NotificationManager对象
     */
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    /**
     * 获取自定义的View
     *
     * @param progress 进度
     * @return RemoteViews
     */
    private RemoteViews getRemoteViews(int progress, String message) {
        if (remoteViews == null) {
            remoteViews = new RemoteViews(this.getPackageName(), R.layout.remote_notification_view);
            remoteViews.setTextViewText(R.id.tvTitle, message);
        }
        remoteViews.setProgressBar(R.id.pb, 100, progress, false);
        return remoteViews;
    }

    /**
     * 创建通知消息体
     *
     * @param progress 进度
     * @return Notification
     */
    private Notification createNotification(int progress) {
        String message = this.getString(R.string.app_update_app);
        if (notification != null) {
            notification.contentView = getRemoteViews(progress, message);
            return notification;
        }

        // android 8.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCustomContentView(getRemoteViews(progress, message))
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            // android 8.0 以下
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setCustomContentView(getRemoteViews(progress, message))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        return notification;
    }


    private Notification createWrongNotification(String message) {
        if (notification != null) {
            RemoteViews remoteViews = getRemoteViews(0, message);
            remoteViews.setTextViewText(R.id.tvTitle, message);
            notification.contentView = remoteViews;
            return notification;
        }

        // android 8.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCustomContentView(getRemoteViews(0, message))
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            // android 8.0 以下
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setCustomContentView(getRemoteViews(0, message))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        return notification;
    }

    /**
     * 显示通知消息
     *
     * @param progress 进度
     */
    public void showNotification(int progress) {
        //下载成功或者失败
        if (progress == 100 || progress == -1) {
            getManager().cancelAll();
        } else {
            getManager().notify(1, createNotification(progress));
        }
    }

    /**
     * 通知错误信息
     *
     * @param message 错误信息
     */
    public void showNotification(String message) {
        getManager().notify(1, createWrongNotification(message));
    }

    /**
     * 清除数据
     */
    private void clear() {
        mManager = null;
        channel = null;
        notification = null;
        remoteViews = null;
    }


}
