package org.sheedon.upgradelibrary.other;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import java.io.File;

/**
 * 下载管理类
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/11 13:26
 */
class DownloadManager {
    private static DownloadManager instance;

    public static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    private DownloadManager() {

    }

    public void downloadSingleTask(String url, File parentFile, String fileName, DownloadListener listener) {

        DownloadTask task = new DownloadTask.Builder(url, parentFile)
                .setFilename(fileName)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(true)
                .build();

        task.enqueue(new SingleDownloadListener(listener));
    }

    public void destroy() {
        instance = null;
    }


    // 单一任务下载监听器
    private static class SingleDownloadListener extends DownloadListener3 {

        private DownloadListener listener;

        SingleDownloadListener(DownloadListener listener) {
            this.listener = listener;
        }

        @Override
        protected void started(DownloadTask task) {
            if (listener != null) {
                listener.start(task);
            }
        }

        @Override
        public void retry(DownloadTask task, ResumeFailedCause cause) {

        }

        @Override
        public void connected(DownloadTask task, int blockCount, long currentOffset, long totalLength) {

        }

        @Override
        public void progress(DownloadTask task, long currentOffset, long totalLength) {
            if (listener != null) {
                long progress = currentOffset * 100 / totalLength;
                listener.progress((int) progress);
            }
        }


        @Override
        protected void completed(DownloadTask task) {
            if (listener != null) {
                listener.completed();
            }
        }

        @Override
        protected void canceled(DownloadTask task) {

        }

        @Override
        protected void error(DownloadTask task, Exception e) {
            if (listener != null) {
                listener.error(e.getMessage());
            }
        }

        @Override
        protected void warn(DownloadTask task) {
            if (listener != null) {
                listener.error("DownloadTask warn");
            }
        }

    }


}
