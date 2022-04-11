package org.sheedon.upgradedispatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.sheedon.upgradelibrary.UpgradeInstaller;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.model.UpgradeTask;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    int versionCode = 13;
    String versionName = "v1.0";
    String apkPath = "http://file.yanhangtec.com/prodectfile/fileResource/upload/a94bd57b-fdb1-4432-8776-293dd04e48d0.apk";
    String desc = "1，添加删除信用卡接口。\r\n2，添加vip认证。\r\n3，区分自定义消费，一个小时不限制。\r\n4，添加放弃任务接口，小时内不生成。\r\n5，消费任务手动生成。";
    String Authorization = "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDk2NTQyNDUsInVzZXJfbmFtZSI6ImFkbWluIiwianRpIjoiY2Q3YmNlMGYtZTJjNC00MDM2LWJkYTAtNTJhODk4N2Y0YjJkIiwidXNlckNvZGUiOiJhZG1pbiIsImNsaWVudF9pZCI6ImVhbSIsInNjb3BlIjpbImFsbCJdfQ.alJ-k-WZoF0HZ89Dgf3vnNYGwAgIoP85oQpUHX0uNMM";

    public void onClick(View view) {
//        UpgradeTaskModel model = new UpgradeTaskModel.Builder(versionCode, versionName, apkPath)
//                .description(desc).build();
//        UpdateDialogFragment.newInstance(model).show(this.getSupportFragmentManager(), "dialog");

        UpgradeTask model = new UpgradeTask.Builder()
                .apkUrl("http://192.168.2.14:3100/api/eam/common/download?id=24")
                .versionName("v10")
                .addHeader("Authorization", Authorization)
                .build();

        UpgradeInstaller.upgradeApp(this, model, new UpgradeListener() {
            @Override
            public void onUpgradeFailure(int code, String message) {
                Log.v("SXD","message"+message);
            }

            @Override
            public void onProgress(int progress) {
                Log.v("SXD","progress"+progress);
            }

            @Override
            public void onStartDownload() {
                Log.v("SXD","onStartDownload");
            }

            @Override
            public void onDownloadSuccess() {
                Log.v("SXD","onDownloadSuccess");
            }
        });

    }

    public void onCancelClick(View view) {
        UpgradeInstaller.cancel(this);
    }
}
