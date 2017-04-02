package com.lingci.entity;

import java.io.Serializable;

public class Like implements Serializable {

    private int uid;
    private String uname;

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

    public Like(int uid, String uname) {
        this.uid = uid;
        this.uname = uname;
    }
}
