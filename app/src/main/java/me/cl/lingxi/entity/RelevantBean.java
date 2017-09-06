package me.cl.lingxi.entity;

import java.util.List;

public class RelevantBean<T> {

    private int unreadnum;
    private List<T> unreadlist ;

    public int getUnreadnum() {
        return unreadnum;
    }

    public void setUnreadnum(int unreadnum) {
        this.unreadnum = unreadnum;
    }

    public List<T> getUnreadlist() {
        return unreadlist;
    }

    public void setUnreadlist(List<T> unreadlist) {
        this.unreadlist = unreadlist;
    }
}
