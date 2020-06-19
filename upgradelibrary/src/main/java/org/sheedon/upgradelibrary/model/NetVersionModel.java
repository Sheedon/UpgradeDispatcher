package org.sheedon.upgradelibrary.model;

/**
 * 网络更新版本内容
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/11 16:36
 */
public class NetVersionModel {
    private int version;
    private String path;

    public static NetVersionModel build(int version, String path) {
        NetVersionModel bean = new NetVersionModel();
        bean.version = version;
        bean.path = path;
        return bean;
    }

    public int getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }
}
