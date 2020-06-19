package org.sheedon.upgradelibrary.other;

import com.liulishuo.okdownload.DownloadTask;

import java.io.File;

/**
 * 下载处理类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/12 8:23
 */
public class DownloadHandle implements DownloadListener {

    private DownloadManager manager;

    private String url;
    private File parentFile;
    private String fileName;
    private DownloadListener listener;

    private int count;
    // 重试次数
    private int reCount;

    public DownloadHandle(int reCount) {
        if (reCount < 0)
            reCount = 0;

        this.count = 0;
        this.reCount = reCount;
        this.manager = DownloadManager.getInstance();
    }

    /**
     * 下载任务
     */
    public void downloadTask(String url, File parentFile, String fileName) {
        this.downloadTask(url, parentFile, fileName, null);
    }

    /**
     * 下载任务
     *
     * @param url        下载地址
     * @param parentFile 报错父目录
     * @param fileName   文件名
     * @param listener   监听器
     */
    public void downloadTask(String url, File parentFile, String fileName, DownloadListener listener) {
        this.url = url;
        this.parentFile = parentFile;
        this.fileName = fileName;
        this.listener = listener;
        manager.downloadSingleTask(url, parentFile, fileName, this);
    }

    @Override
    public void start(DownloadTask task) {
        if (listener != null) {
            listener.start(task);
        }
    }

    @Override
    public void progress(int progress) {
        if (listener != null) {
            listener.progress(progress);
        }
    }

    @Override
    public void completed() {
        if (listener != null) {
            listener.completed();
        }
    }

    @Override
    public void error(String message) {
        if (reDownload()) {
            return;
        }
        if (listener != null) {
            listener.error(message);
        }
    }

    /**
     * 重新下载
     */
    private boolean reDownload() {
        if (++count > reCount) {
            return false;
        }

        downloadTask(url, parentFile, fileName, listener);
        return true;
    }

    /**
     * 销毁
     */
    public void destroy() {
        url = null;
        parentFile = null;
        fileName = null;
        listener = null;
        manager = null;
    }
}
