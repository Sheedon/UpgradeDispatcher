package org.sheedon.upgradedispatcher;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.sheedon.upgradelibrary.UpgradeInstaller;

/**
 * java类作用描述
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/15 13:07
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        // 升级模块初始化
        UpgradeInstaller.setUp(this, () -> Log.v("SXD", "onCompleted"));
    }

    public static App getInstance() {
        return instance;
    }
}
