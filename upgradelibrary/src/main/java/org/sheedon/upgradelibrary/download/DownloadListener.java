package org.sheedon.upgradelibrary.download;

import com.liulishuo.okdownload.DownloadTask;

/**
 * 自定义下载监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 4:11 下午
 */
public interface DownloadListener {
    void start(DownloadTask task);

    void progress(int progress);

    void completed();

    void error(String message);
}
