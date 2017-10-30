package me.cl.lingxi.entity;

import java.io.Serializable;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/10/26
 * desc   : App版本信息
 * version: 1.0
 */

public class AppVersion implements Serializable{

    private String version_name;
    private int version_code;
    private String apk_name;
    private String apk_url;
    private int update_flag;
    private String update_info;

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getApk_name() {
        return apk_name;
    }

    public void setApk_name(String apk_name) {
        this.apk_name = apk_name;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public int getUpdate_flag() {
        return update_flag;
    }

    public void setUpdate_flag(int update_flag) {
        this.update_flag = update_flag;
    }

    public String getUpdate_info() {
        return update_info;
    }

    public void setUpdate_info(String update_info) {
        this.update_info = update_info;
    }
}
