package me.cl.lingxi.entity;

import java.io.Serializable;

public class Relevant<T> implements Serializable {

    private int uid;
    private String uname;
    private String url;
    private int cmid;
    private String cm_time;
    private String comment;
    private T minifeed;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public String getCm_time() {
        return cm_time;
    }

    public void setCm_time(String cm_time) {
        this.cm_time = cm_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public T getMinifeed() {
        return minifeed;
    }

    public void setMinifeed(T minifeed) {
        this.minifeed = minifeed;
    }
}