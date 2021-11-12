package org.sheedon.upgradelibrary.manager;

import android.content.Context;

import org.sheedon.upgradelibrary.listener.DispatchListener;
import org.sheedon.upgradelibrary.utils.ApkUtils;

import java.io.File;

/**
 * 默认安装管理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 11:40 上午
 */
public class DefaultInstallerManager implements InstallerManagerCenter {

    // 上下文
    private final Context context;

    public DefaultInstallerManager(Context context) {
        this.context = context;
    }

    @Override
    public void attachListener(DispatchListener listener) {
        // 执行监听器
    }

    @Override
    public boolean install(File downloadFile) {
        return ApkUtils.installApk(context, downloadFile);
    }
}
