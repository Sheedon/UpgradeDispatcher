package org.sheedon.upgradelibrary.listener;

import java.io.File;

/**
 * 调度监听
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 6:23 下午
 */
public interface DispatchListener {

    /**
     * 执行下一个任务
     */
    void doNext();

    /**
     * 开启下载任务
     */
    void onStartTask();

    /**
     * 通知进度
     *
     * @param progress 进度
     */
    void onProgress(int progress);

    /**
     * 通知更新失败
     *
     * @param code    失败编码
     * @param message 错误描述
     */
    void onUpgradeFailure(int code, String message);

    /**
     * 下载完成
     *
     * @param downloadFile 下载文件
     */
    void onDownloadCompleted(File downloadFile);
}
