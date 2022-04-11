package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.listener.DispatchListener;
import org.sheedon.upgradelibrary.model.UpgradeTask;

import java.util.Map;

/**
 * 下载处理中心
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 10:35 上午
 */
public interface DownloadManagerCenter {


    /**
     * 是否允许中
     */
    boolean isRunning();


    /**
     * 附加监听器
     *
     * @param listener 监听器
     */
    void attachListener(DispatchListener listener);

    /**
     * 核实是否有存储权限
     */
    void checkPermission(Context context);

    /**
     * 附加更新任务
     *
     * @param task 更新任务
     */
    void attachTask(UpgradeTask task);

    /**
     * 核实本地Apk是否有符合条件的，
     * 若有，则直接安装，并且删除其他无效的
     * 若无，直接删除所有无效的
     */
    boolean checkLocalApk();

    /**
     * 下载网络Apk
     */
    void downloadApk();

    /**
     * 取消
     */
    void cancel();
}
