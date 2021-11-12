package org.sheedon.upgradelibrary.model;

import java.util.Objects;

/**
 * 更新任务model
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 11:24 上午
 */
public class UpgradeTaskModel {

    private final String dialogTitle;
    // 版本号
    private final int versionCode;
    // 版本名称
    private final String versionName;
    // apk 下载地址
    private final String apkPath;
    // 描述信息
    private final String description;
    //是否强制更新
    private final boolean constraint;

    private UpgradeTaskModel(Builder builder) {
        this.dialogTitle = builder.dialogTitle;
        this.versionCode = builder.versionCode;
        this.versionName = builder.versionName;
        this.apkPath = builder.apkPath;
        this.description = builder.description;
        this.constraint = builder.constraint;
    }


    public String getDialogTitle() {
        return dialogTitle;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isConstraint() {
        return constraint;
    }

    public String getApkPath() {
        return apkPath;
    }

    public static class Builder {
        // dialog标题内容 替换是否升级到xx版本
        private String dialogTitle;
        // 版本号
        private final int versionCode;
        // 版本名称
        private final String versionName;
        // 下载路径
        private final String apkPath;
        // 描述信息
        private String description;
        //是否强制更新 暂定
        private boolean constraint;

        public Builder(int versionCode, String versionName, String apkPath) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.apkPath = apkPath;
        }

        public Builder dialogTitle(String dialogTitle) {
            this.dialogTitle = Objects.requireNonNull(dialogTitle, "dialogTitle == null");
            return this;
        }

        public Builder description(String description) {
            this.description = Objects.requireNonNull(description, "description == null");
            return this;
        }

        public Builder constraint(boolean constraint) {
            this.constraint = constraint;
            return this;
        }

        public UpgradeTaskModel build() {
            return new UpgradeTaskModel(this);
        }
    }
}
