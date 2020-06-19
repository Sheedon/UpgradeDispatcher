package org.sheedon.upgradelibrary.other;

import com.liulishuo.okdownload.DownloadTask;

/**
 * 自定义下载监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/11 13:56
 */
public interface DownloadListener {

    void start(DownloadTask task);

    void progress(int progress);

    void completed();

    void error(String message);
}
