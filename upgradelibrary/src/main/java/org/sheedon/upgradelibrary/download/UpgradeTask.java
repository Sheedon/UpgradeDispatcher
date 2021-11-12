package org.sheedon.upgradelibrary.download;

import org.sheedon.upgradelibrary.UpgradeConstants;

import java.io.File;

/**
 * 更新任务
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 4:17 下午
 */
public class UpgradeTask {


    private final File parentFile;

    private final String netUrl;
    private final String fileName;
    private final int reCount;

    private UpgradeTask(String netUrl, File dirFile, String fileName, int reCount) {
        this.netUrl = netUrl;
        this.parentFile = dirFile;
        this.fileName = convertFileName(fileName);
        this.reCount = reCount;
    }

    /**
     * 转化文件名为 xxx.apk
     *
     * @param fileName 源文件名
     * @return 有后缀.apk 的文件名
     */
    private String convertFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return UpgradeConstants.APK_NAME;
        }

        if (fileName.contains(UpgradeConstants.SUFFIX_DOT)) {
            return fileName;
        }
        return fileName + UpgradeConstants.SUFFIX_DOT;
    }

    public File getParentFile() {
        return parentFile;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public int getReCount() {
        return reCount;
    }

    public static class Builder {

        // 网络资源地址
        String netUrl;
        // 文件夹
        File dirFile;
        // 文件名
        String fileName;

        int reCount;

        public Builder() {
            reCount = 5;
        }

        /**
         * 网络地址
         *
         * @param netUrl 网络地址
         * @return Builder
         */
        public Builder netUrl(String netUrl) {
            if (netUrl == null || netUrl.isEmpty()) {
                throw new RuntimeException("netUrl cannot null");
            }
            this.netUrl = netUrl;
            return this;
        }

        public Builder dirFile(File dirFile) {
            if (dirFile == null) {
                throw new RuntimeException("dirFile cannot null");
            }
            this.dirFile = dirFile;
            return this;
        }

        /**
         * 下载文件名
         */
        public Builder downloadFileName(String fileName) {
            if (fileName == null) {
                throw new RuntimeException("fileName cannot null");
            }
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
            return new UpgradeTask(netUrl, dirFile, fileName, reCount);
        }

    }
}
