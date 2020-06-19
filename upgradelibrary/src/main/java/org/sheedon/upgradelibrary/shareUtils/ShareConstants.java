package org.sheedon.upgradelibrary.shareUtils;

import android.os.Environment;

/**
 * 共享数据
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/14 8:33
 */
public class ShareConstants {

    public static final int STATUS_NORMAL = -99;// 正常模式，未开启更新

    public static final int STATUS_INIT_WAKE_COMPLETE = 1000;// 【唤醒APP】正常完成
    public static final int STATUS_INIT_WAKE_INSTALLED = 1001;//【唤醒APP】已更新
    public static final int STATUS_INIT_FILE_CREATION_FAILED = 1002;//创建文件失败
    public static final int STATUS_INIT_FILE_EXPORT_FAILED = 1003;//导出失败
    public static final int STATUS_INIT_FILE_PERMISSION_DENIED = 1004;//没有权限
    public static final int STATUS_INIT_INSTALLATION_FAILED = 1005;//安装失败
    public static final int STATUS_INIT_NOTIFICATION_TIMEOUT = 1006;//通知超时
    public static final int STATUS_UPGRADE_NOTIFICATION_TIMEOUT = 1007;//升级通知超时
    public static final int STATUS_UPGRADE_RECEIVED = 1008;//升级通知唤醒核实完成
    public static final int STATUS_INIT_DOWNLOAD_COMPLETE = 1009;//下载模块配置完成
    public static final int STATUS_INIT_INSTALL_COMPLETE = 1010;//安装模块配置完成
    public static final int STATUS_UPGRADE_PARAMETER_ERROR = 1011;//更新参数有误
    public static final int STATUS_UPGRADE_GREATER_THAN_CURRENT_VERSION = 1012;//必须大于当前版本
    public static final int STATUS_UPGRADE_DOWNLOAD_FAIL = 1013;//下载失败
    public static final int STATUS_UPGRADE_DOWNLOAD_COMPLETE = 1014;//下载完成
    public static final int STATUS_UPGRADE_DOWNLOAD_START = 1015;//开始下载
    public static final int STATUS_UPGRADE_INSTALL_START = 1016;//开始安装
    public static final int STATUS_UPGRADE_INSTALL_FAILED = 1017;//安装失败
    public static final int STATUS_UPGRADE_INSTALLED = 1018;//安装成功


    // 唤醒App包名
    public static final String WAKE_APP_PACKAGE = "org.sheedon.apkreceiver";

    // 根目录
    public static final String ROOT_DRI = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 更新App下载目录
    public static final String UPDATE_APP_PATH = ROOT_DRI + "/update";
    // 唤醒App下载地址
    public static final String WAKE_APP_PATH = ROOT_DRI + "/wake";
    public static final String WAKE_APP_NAME = "message.apk";

}
