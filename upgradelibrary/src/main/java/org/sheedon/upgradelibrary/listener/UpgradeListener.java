package org.sheedon.upgradelibrary.listener;

/**
 * 升级监听器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/16 11:07
 */
public interface UpgradeListener {

    void onProgress(int progress);

    void onUpgradeError(String message);

    void onUpgradeStatus(int status);
}
