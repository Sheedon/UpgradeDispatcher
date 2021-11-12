package org.sheedon.upgradelibrary.download;

import com.liulishuo.okdownload.DownloadTask;

/**
 * 下载执行者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 4:36 下午
 */
public class DownloadHandler implements DownloadListener {

    private DownloadManager manager;

    private UpgradeTask task;

    private DownloadListener listener;

    private int index;
    // 重试次数
    private int reCount;

    public DownloadHandler(UpgradeTask task, DownloadListener listener) {
        this.task = task;
        this.listener = listener;

        this.index = 0;
        this.reCount = task.getReCount();
        this.manager = DownloadManager.getInstance();
    }

    /**
     * 下载任务
     */
    public void downloadTask() {
        index++;
        manager.downloadSingleTask(task.getNetUrl(), task.getParentFile(), task.getFileName(), this);
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
        if (index > reCount) {
            return false;
        }

        downloadTask();
        return true;
    }

    public void cancel(){
        if (manager != null) {
            manager.cancel();
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        task = null;
        listener = null;
        manager = null;
    }
}
