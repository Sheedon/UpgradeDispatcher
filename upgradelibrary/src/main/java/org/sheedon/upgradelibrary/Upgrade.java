package org.sheedon.upgradelibrary;

import android.annotation.SuppressLint;
import android.content.Context;

import org.sheedon.upgradelibrary.listener.InitializeListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.manager.DefaultDownloadManager;
import org.sheedon.upgradelibrary.manager.DefaultInstallerManager;
import org.sheedon.upgradelibrary.manager.DefaultNotifyManager;
import org.sheedon.upgradelibrary.manager.DownloadManagerCenter;
import org.sheedon.upgradelibrary.manager.InstallerManagerCenter;
import org.sheedon.upgradelibrary.model.UpgradeTask;

/**
 * App升级核心单例类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 10:06 上午
 */
public final class Upgrade {
    private static final String TAG = "Upgrade.Upgrade";

    @SuppressLint("StaticFieldLeak")
    private static volatile Upgrade sInstance;

    private Context context;
    // 初始化监听器
    private InitializeListener initializeListener;
    // 下载模块
    private DownloadManagerCenter downloadManagerCenter;
    // 安装模块
    private InstallerManagerCenter installerManagerCenter;
    // 升级执行器
    private UpgradeDispatcher upgradeDispatcher;
    // 通知窗体工厂类
    private UpgradeListener.UpgradeNotifyFactory notifyFactory;

    private Upgrade(Builder builder) {
        this.context = builder.context;
        this.initializeListener = builder.initializeListener;
        this.downloadManagerCenter = builder.downloadManagerCenter;
        this.installerManagerCenter = builder.installerManagerCenter;
        this.notifyFactory = builder.notifyFactory;
        this.upgradeDispatcher = new UpgradeDispatcher(context,
                downloadManagerCenter, installerManagerCenter);
        sInstance = this;
    }

    /**
     * 使用默认配置升级进行初始化
     *
     * @param context 这里会被转化为ApplicationContext
     * @return the Upgrade object
     */
    public static Upgrade with(Context context) {
        if (sInstance == null) {
            synchronized (Upgrade.class) {
                if (sInstance == null) {
                    sInstance = new Builder(context).build();
                }
            }
        }
        return sInstance;
    }

    /**
     * 升级App
     */
    public void upgradeApp(Context context, UpgradeTask model, UpgradeListener listener) {
        if (listener == null) {
            listener = notifyFactory.createWindow(context);
        }
        if (upgradeDispatcher != null) {
            upgradeDispatcher.upgradeApp(context, model, listener);
            return;
        }
        if (listener != null) {
            listener.onUpgradeFailure(UpgradeListener.TYPE_ERROR, "");
        }

    }

    /**
     * 取消操作
     */
    public void cancel() {
        if (upgradeDispatcher != null) {
            upgradeDispatcher.cancel();
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        context = null;
        initializeListener = null;
        downloadManagerCenter = null;
        installerManagerCenter = null;
        upgradeDispatcher = null;
        sInstance = null;
    }

    public static class Builder {

        private final Context context;
        // 初始化监听器
        private InitializeListener initializeListener;
        // 下载模块
        private DownloadManagerCenter downloadManagerCenter;
        // 安装模块
        private InstallerManagerCenter installerManagerCenter;
        // 通知窗体工厂类
        private UpgradeListener.UpgradeNotifyFactory notifyFactory;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * 初始化监听器
         *
         * @param listener 初始化监听器
         * @return Builder
         */
        public Builder initializeListener(InitializeListener listener) {
            if (listener == null) {
                throw new RuntimeException("InitializeListener cannot null");
            }
            this.initializeListener = listener;
            return this;
        }

        /**
         * 下载管理中心
         *
         * @param center 下载管理中心
         * @return Builder
         */
        public Builder downloadManagerCenter(DownloadManagerCenter center) {
            if (center == null) {
                throw new RuntimeException("DownloadManagerCenter cannot null");
            }
            this.downloadManagerCenter = center;
            return this;
        }

        /**
         * 安装管理中心
         *
         * @param center 安装管理中心
         * @return Builder
         */
        public Builder installerManagerCenter(InstallerManagerCenter center) {
            if (center == null) {
                throw new RuntimeException("InstallerManagerCenter cannot null");
            }
            this.installerManagerCenter = center;
            return this;
        }

        /**
         * 通知展示的工厂创建类
         *
         * @param factory 工厂类
         * @return Builder
         */
        public Builder notifyFactory(UpgradeListener.UpgradeNotifyFactory factory) {
            if (factory == null) {
                throw new RuntimeException("factory cannot null");
            }
            this.notifyFactory = factory;
            return this;
        }

        public Upgrade build() {
            if (downloadManagerCenter == null) {
                downloadManagerCenter = new DefaultDownloadManager(context);
            }

            if (installerManagerCenter == null) {
                installerManagerCenter = new DefaultInstallerManager(context);
            }

            if (notifyFactory == null) {
                notifyFactory = new DefaultNotifyManager();
            }
            return new Upgrade(this);
        }
    }
}
