package org.sheedon.upgradelibrary.manager;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.liulishuo.okdownload.DownloadTask;

import org.sheedon.upgradelibrary.R;
import org.sheedon.upgradelibrary.UpgradeConstants;
import org.sheedon.upgradelibrary.download.DownloadHandler;
import org.sheedon.upgradelibrary.download.DownloadListener;
import org.sheedon.upgradelibrary.download.UpgradeTask;
import org.sheedon.upgradelibrary.listener.DispatchListener;
import org.sheedon.upgradelibrary.listener.UpgradeListener;
import org.sheedon.upgradelibrary.utils.ApkUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认下载管理者
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2021/10/25 11:34 上午
 */
public final class DefaultDownloadManager implements DownloadManagerCenter
        , OnPermissionCallback {

    // 执行监听器
    private DispatchListener listener;
    // 上下文
    private final Context context;
    // 权限
    private static final String[] PERMISSIONS = new String[]{Permission.REQUEST_INSTALL_PACKAGES,
            Permission.NOTIFICATION_SERVICE, Permission.MANAGE_EXTERNAL_STORAGE};

    // 是否运行中
    private boolean running;

    private final UpgradeTask.Builder taskBuilder;

    private final AtomicBoolean cancel = new AtomicBoolean(false);
    // 下载执行器
    private DownloadHandler handler;

    public DefaultDownloadManager(Context context) {
        this.context = context;
        taskBuilder = new UpgradeTask.Builder();
    }


    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * 附加升级监听器
     *
     * @param listener 监听器
     */
    @Override
    public void attachListener(DispatchListener listener) {
        this.listener = listener;
    }

    /**
     * 核实升级安装权限
     * 1。安装权限
     * 2。通知栏权限
     * 3。存储权限
     */
    @Override
    public void checkPermission(Context context) {
        running = true;
        XXPermissions.with(context)
                // 申请权限
                .permission(PERMISSIONS)
                .request(this);
    }

    /**
     * 核实本地apk是否符合安装条件，
     * 若存在直接安装，
     * 否则下载操作
     *
     * @return 是否需要下载
     */
    @Override
    public boolean checkLocalApk(String netName) {
        // 根据包名获取，当前包所放在的下载路径
        String lastName = ApkUtils.getPackageLastName(context);
        String downloadFile = UpgradeConstants.UPDATE_APP_PATH + lastName;

        // 构建生成路径
        File parentFile = new File(downloadFile);
        if (!parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdirs();
        }

        // 填充父级路径和下载的文件名（版本名）
        taskBuilder.dirFile(parentFile)
                .downloadFileName(netName);

        File[] files = parentFile.listFiles();
        // 没有文件，则需要下载
        if (files == null || files.length == 0) {
            return true;
        }

        // 是否存在目标文件
        boolean hasTargetFile = false;

        // 遍历删除
        for (File file : files) {
            if (file == null) {
                continue;
            }

            boolean apkFile = ApkUtils.isApkFile(file, netName);
            if (!apkFile) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

            PackageInfo info = ApkUtils.getApkPackageInfo(context, file);
            if (info != null) {
                hasTargetFile = true;
            }

        }

        return hasTargetFile;
    }

    /**
     * 下载Apk
     *
     * @param netUrl 网络下载URL
     */
    @Override
    public void downloadApk(String netUrl) {
        if (cancel.get()) {
            return;
        }

        UpgradeTask task = taskBuilder.netUrl(netUrl).build();
        handler = new DownloadHandler(task, new DownloadListener() {
            @Override
            public void start(DownloadTask task) {
                if(listener != null){
                    listener.onStartTask();
                }
            }

            @Override
            public void progress(int progress) {
                if (listener == null) {
                    return;
                }

                listener.onProgress(progress);
            }

            @Override
            public void completed() {
                running = false;
                if (listener == null) {
                    return;
                }

                listener.onDownloadCompleted(new File(task.getParentFile(), task.getFileName()));
            }

            @Override
            public void error(String message) {
                notifyUpgradeFailure(UpgradeListener.TYPE_DOWNLOAD_FAILURE,
                        message);
            }
        });
        handler.downloadTask();

    }

    /**
     * 取消
     */
    @Override
    public void cancel() {
        cancel.set(true);
        if (handler != null) {
            handler.cancel();
        }
    }


    /**
     * 授予权限
     *
     * @param permissions 权限内容
     * @param all         是否所有
     */
    @Override
    public void onGranted(List<String> permissions, boolean all) {
        if (all) {
            if (listener == null) {
                return;
            }

            listener.doNext();
        } else {
            notifyUpgradeFailure(UpgradeListener.TYPE_PERMISSION,
                    context.getString(R.string.app_permission));
        }
    }

    /**
     * 权限被拒绝
     *
     * @param permissions 权限内容
     * @param never       是否永久拒绝
     */
    @Override
    public void onDenied(List<String> permissions, boolean never) {
        notifyUpgradeFailure(UpgradeListener.TYPE_PERMISSION,
                context.getString(R.string.app_permission));
    }

    /**
     * 通知升级结果
     *
     * @param code    错误编码
     * @param message 描述信息
     */
    private void notifyUpgradeFailure(int code, String message) {
        running = false;
        if (listener == null) {
            return;
        }

        listener.onUpgradeFailure(code, message);
    }
}
