package me.cl.lingxi.entity;

import java.util.List;

public class UserBean<T> {

    private int usernum;
    private List<T> userlist;

    public int getUsernum() {
        return usernum;
    }

    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public List<T> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<T> userlist) {
        this.userlist = userlist;
    }
}
