package org.sheedon.upgradelibrary.listener;

import org.sheedon.upgradelibrary.model.UpgradeVersionModel;

/**
 * 初始化监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/13 22:31
 */
public interface InstallListener {

    /**
     * 更新结果
     *
     * @param model 更新版本数据
     */
    void onResultCallback(UpgradeVersionModel model);
}
