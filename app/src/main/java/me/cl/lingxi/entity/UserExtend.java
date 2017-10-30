package me.cl.lingxi.entity;

import java.util.List;

public class UserExtend<T> {

    private int usernum;
    private List<User> userlist;

    public int getUsernum() {
        return usernum;
    }

    public void setUsernum(int usernum) {
        this.usernum = usernum;
    }

    public List<User> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<User> userlist) {
        this.userlist = userlist;
    }
}
