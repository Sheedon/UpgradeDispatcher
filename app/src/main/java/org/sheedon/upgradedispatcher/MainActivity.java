package org.sheedon.upgradedispatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import org.sheedon.upgradelibrary.UpgradeInstaller;
import org.sheedon.upgradelibrary.listener.InstallListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.model.UpgradeVersionModel;
import org.sheedon.upgradelibrary.other.UpgradeTask;
import org.sheedon.upgradelibrary.model.NetVersionModel;

public class MainActivity extends AppCompatActivity {

    private UpgradeTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUsagePermission();
        init();
    }

    private void init() {
        UpgradeInstaller.setUp(App.getInstance(), new InstallListener() {
            @Override
            public void onResultCallback(UpgradeVersionModel model) {

            }
        });
    }

    public void onClick(View view) {
//        manager.setUpTask(task, new UpgradeListener() {
//        });
//        ApkUtils.sendBroadcast(this,"com.test.test");
//
//        PluginManager.getInstance().installPlugin();
        task = new UpgradeTask.Builder(this, NetVersionModel.build(2, "https://yanhang-file.oss-cn-hangzhou.aliyuncs.com/apk/%E6%9B%B4%E6%96%B0demo.apk"))
                .build();
        UpgradeInstaller.onReceiveUpgradeInfo(this, task, new UpgradeListener() {
            @Override
            public void onProgress(int progress) {
                Log.v("UpgradeListener", "progress:" + progress);
            }

            @Override
            public void onUpgradeError(String message) {
                Log.v("UpgradeListener", "message:" + message);
            }

            @Override
            public void onUpgradeStatus(int status) {
                Log.v("UpgradeListener", "status:" + status);
            }
        });

    }

    public void onCancelClick(View view){
        UpgradeInstaller.cancel(this);
    }

    private boolean checkUsagePermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        if (!granted) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, 1);
            return false;
        }
        return true;
    }

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!checkUsagePermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }
}
