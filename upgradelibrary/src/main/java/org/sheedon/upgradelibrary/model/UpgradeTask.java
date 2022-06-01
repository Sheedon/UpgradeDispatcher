package org.sheedon.upgradelibrary.model;

import org.sheedon.upgradelibrary.UpgradeConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private final Map<String, String> headers;
    private final boolean breakpoint;

    private UpgradeTask(Builder builder) {
        this.netUrl = builder.netUrl;
        this.parentFile = builder.dirFile;
        this.fileName = convertFileName(builder.fileName);
        this.reCount = builder.reCount;
        this.headers = builder.headers;
        this.breakpoint = builder.breakpoint;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getReCount() {
        return reCount;
    }

    public boolean isBreakpoint() {
        return breakpoint;
    }

    public static class Builder {

        // 网络资源地址
        String netUrl;
        // 文件夹
        File dirFile;
        // 文件名
        String fileName;
        // 请求头部
        Map<String, String> headers = new HashMap<>();

        int reCount;


        // 是否启用断点重传机制
        boolean breakpoint = true;

        public Builder() {
            reCount = 5;
        }

        /**
         * 网络地址
         *
         * @param apkUrl 网络地址
         * @return Builder
         */
        public Builder apkUrl(String apkUrl) {
            if (apkUrl == null || apkUrl.isEmpty()) {
                throw new RuntimeException("apkUrl cannot null");
            }
            this.netUrl = apkUrl;
            return this;
        }

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
         * 版本号，充当文件名
         */
        public Builder versionName(String versionName) {
            this.fileName = Objects.requireNonNull(versionName, "versionName == null");
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

        public Builder headers(Map<String, String> headers) {
            this.headers = Objects.requireNonNull(headers, "headers == null");
            return this;
        }

        public Builder addHeader(String key, String value) {
            String targetKey = Objects.requireNonNull(key, "key == null");
            String targetValue = Objects.requireNonNull(value, "value == null");
            this.headers.put(targetKey, targetValue);
            return this;
        }

        public Builder breakpoint(boolean breakpoint) {
            this.breakpoint = breakpoint;
            return this;
        }


        public UpgradeTask build() {
            Objects.requireNonNull(netUrl, "netUrl == null");
            Objects.requireNonNull(fileName, "fileName == null");

            return new UpgradeTask(this);
        }

    }
}
