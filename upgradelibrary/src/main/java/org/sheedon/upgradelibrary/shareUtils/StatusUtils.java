package org.sheedon.upgradelibrary.shareUtils;

/**
 * 状态工具
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/16 11:12
 */
public class StatusUtils {

    public static String convertStatus(int status) {
        switch (status) {

            case 1000:
                return "【唤醒APP】正常完成";
            case 1001:
                return "【唤醒APP】已更新";
            case 1002:
                return "升级通知唤醒核实完成";
            case 1003:
                return "下载模块配置完成";
            case 1004:
                return "安装模块配置完成";
            case 1005:
                return "下载完成";
            case 1006:
                return "开始下载";
            case 1007:
                return "开始安装";
            case 1008:
                return "安装成功";
            case 1009:
                return "取消更新";

            case -99:
                return "正常模式，未开启更新";
            case -1000:
                return "创建文件失败";
            case -1001:
                return "导出失败";
            case -1002:
                return "没有权限";
            case -1003:
                return "[唤醒App]安装失败";
            case -1004:
                return "通知超时";
            case -1005:
                return "升级通知超时";
            case -1006:
                return "更新参数有误";
            case -1007:
                return "必须大于当前版本";
            case -1008:
                return "下载失败";
            case -1009:
                return "安装失败";
        }
        return "" + status;
    }
}
