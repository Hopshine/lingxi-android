package me.cl.lingxi.entity;

import java.io.Serializable;
import java.util.List;

public class Evaluate implements Serializable {

    private int cmid;
    private int uid;
    private String uname;
    private String url;
    private String comment;
    private String cm_time;
    private List<Reply> replylist;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCm_time() {
        return cm_time;
    }

    public void setCm_time(String cm_time) {
        this.cm_time = cm_time;
    }

    public List<Reply> getReplylist() {
        return replylist;
    }

    public void setReplylist(List<Reply> replylist) {
        this.replylist = replylist;
    }
}
