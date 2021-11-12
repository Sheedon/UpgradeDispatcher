package org.sheedon.upgradedispatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.sheedon.upgradelibrary.UpgradeInstaller;
import org.sheedon.upgradelibrary.model.UpgradeTaskModel;
import org.sheedon.upgradelibrary.view.UpdateDialogFragment;


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


    public void onClick(View view) {
        UpgradeTaskModel model = new UpgradeTaskModel.Builder(versionCode, versionName, apkPath)
                .description(desc).build();
        UpdateDialogFragment.newInstance(model).show(this.getSupportFragmentManager(), "dialog");

    }

    public void onCancelClick(View view) {
        UpgradeInstaller.cancel(this);
    }
}
