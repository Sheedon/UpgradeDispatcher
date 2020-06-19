package org.sheedon.upgradelibrary.shareUtils;

/**
 * 状态错误
 * 主要用于结束Observable发送
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/15 9:35
 */
public class UpgradeStatusException extends Throwable {

    public UpgradeStatusException(int status) {
        super(String.valueOf(status));
    }

    public UpgradeStatusException(String message) {
        super(message);
    }
}
