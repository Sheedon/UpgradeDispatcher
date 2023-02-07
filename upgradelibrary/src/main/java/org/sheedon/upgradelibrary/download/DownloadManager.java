package org.sheedon.upgradelibrary.download;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import org.sheedon.upgradelibrary.model.UpgradeTask;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 下载管理器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 4:02 下午
 */
class DownloadManager {
    private static DownloadManager instance;

    private DownloadTask task;
    private boolean breakpoint;

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

    public void downloadSingleTask(UpgradeTask upgradeTask, DownloadListener listener) {

        DownloadTask.Builder builder = new DownloadTask.Builder(upgradeTask.getNetUrl(), upgradeTask.getParentFile())
                .setFilename(upgradeTask.getFileName())
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(true);


        Map<String, String> handlers = upgradeTask.getHeaders();
        for (Map.Entry<String, String> entry : handlers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        breakpoint = upgradeTask.isBreakpoint();

        task = builder.build();
        task.enqueue(new SingleDownloadListener(listener));
    }

    public void cancel() {
        task.cancel();
    }

    public void destroy() {
        instance = null;
    }


    // 单一任务下载监听器
    private class SingleDownloadListener extends DownloadListener3 {

        private final DownloadListener listener;

        // false:未完成，true：等待中，两次false，已完成
        private final AtomicBoolean flag = new AtomicBoolean();
        private final Handler handler = new Handler();

        SingleDownloadListener(DownloadListener listener) {
            this.listener = listener;
        }

        @Override
        protected void started(@NonNull DownloadTask task) {
            if (listener != null) {
                listener.start(task);
            }
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            if (listener != null) {
                if (totalLength == 0) {
                    totalLength = (currentOffset + 1) * 100;
                }
                long progress = currentOffset * 100 / totalLength;
                listener.progress((int) progress);

                if (progress >= 100 && !flag.get()) {
                    flag.set(true);
                    handler.postDelayed(runnable, 50);
                }
            }
        }


        @Override
        protected void completed(@NonNull DownloadTask task) {
            if (listener != null) {
                listener.completed();
            }
        }

        @Override
        protected void canceled(@NonNull DownloadTask task) {

        }

        @Override
        protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
            if (listener != null) {
                listener.error(e.getMessage());
            }
        }

        @Override
        protected void warn(@NonNull DownloadTask task) {
            if (listener != null) {
                listener.error("DownloadTask warn");
            }
        }

        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (flag.get()) {
                    flag.set(false);
                    handler.postDelayed(runnable, 100);
                    return;
                }

                if (!flag.get()) {
                    if (listener != null) {
                        listener.completed();
                    }
                }
            }
        };

    }

}
