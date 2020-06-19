package org.sheedon.upgradelibrary.other;

import android.content.Context;
import android.os.Environment;

import org.sheedon.upgradelibrary.model.NetVersionModel;
import org.sheedon.upgradelibrary.shareUtils.ApkUtils;
import org.sheedon.upgradelibrary.shareUtils.ShareConstants;

import java.io.File;

/**
 * 更新任务
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/11 16:13
 */
public class UpgradeTask {
    private final int localVersion;
    private final String localPackageName;
    private final File parentFile;

    private final NetVersionModel netVersionModel;
    private final String fileName;
    private final int reCount;

    private UpgradeTask(int localVersion, String localPackageName,
                        NetVersionModel netVersionModel,
                        File parentFile, String fileName, int reCount) {
        this.localVersion = localVersion;
        this.localPackageName = localPackageName;
        this.parentFile = parentFile;
        this.netVersionModel = netVersionModel;
        this.fileName = fileName;
        this.reCount = reCount;
    }

    public int getLocalVersion() {
        return localVersion;
    }

    public String getLocalPackageName() {
        return localPackageName;
    }

    public File getParentFile() {
        return parentFile;
    }

    public NetVersionModel getNetVersionModel() {
        return netVersionModel;
    }

    public String getFileName() {
        return fileName;
    }

    public int getReCount() {
        return reCount;
    }

    public static class Builder {

        final int localVersion;
        final String localPackageName;
        final File parentFile;

        NetVersionModel netVersionModel;
        String fileName;
        int reCount;

        public Builder(Context context, NetVersionModel bean) {
            localVersion = ApkUtils.getVersionCode(context);
            localPackageName = ApkUtils.getPackageName(context);

            parentFile = new File(ShareConstants.UPDATE_APP_PATH);

            netVersionModel = bean;
            fileName = getFileName();
            reCount = 5;
        }

        /**
         * 文件名
         */
        private String getFileName() {
            if (netVersionModel == null || netVersionModel.getPath() == null || netVersionModel.getPath().trim().equals(""))
                return System.currentTimeMillis() + ".apk";

            String path = netVersionModel.getPath();
            String[] split = path.split("/");
            for (int index = split.length - 1; index >= 0; index--) {
                if (!split[index].equals(""))
                    return split[index];
            }

            return System.currentTimeMillis() + ".apk";

        }

        /**
         * 下载文件名
         */
        public Builder downloadFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 重试次数
         */
        public Builder reCount(int reCount) {
            if (reCount < 0)
                reCount = 0;

            this.reCount = reCount;
            return this;
        }


        public UpgradeTask build() {
            return new UpgradeTask(localVersion, localPackageName,
                    netVersionModel,
                    parentFile, fileName, reCount);
        }

    }
}
