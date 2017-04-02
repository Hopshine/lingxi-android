package com.lingci.entity;

import java.io.Serializable;

public class Reply implements Serializable {

    private int rpid;
    private int cmid;
    private int uid;
    private String uname;
    private int touid;
    private String touname;
    private String reply;
    private String rp_time;

    public int getRpid() {
        return rpid;
    }

    public void setRpid(int rpid) {
        this.rpid = rpid;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getTouid() {
        return touid;
    }

    public void setTouid(int touid) {
        this.touid = touid;
    }

    public String getTouname() {
        return touname;
    }

    public void setTouname(String touname) {
        this.touname = touname;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getRp_time() {
        return rp_time;
    }

    public void setRp_time(String rp_time) {
        this.rp_time = rp_time;
    }
}
