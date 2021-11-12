package org.sheedon.upgradelibrary.manager;

import org.sheedon.upgradelibrary.listener.DispatchListener;

import java.io.File;

/**
 * 安装管理中心
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 10:57 上午
 */
public interface InstallerManagerCenter {

    /**
     * 附加更新App监听器
     *
     * @param listener 监听器
     */
    void attachListener(DispatchListener listener);

    /**
     * 执行安装
     */
    boolean install(File downloadFile);
}
