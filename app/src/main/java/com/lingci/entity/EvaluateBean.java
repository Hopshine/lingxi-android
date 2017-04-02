package com.lingci.entity;

import java.util.List;

public class EvaluateBean<T> {

    private int totalnum;
    private List<T> cmtlist;

    private int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public List<T> getCmtlist() {
        return cmtlist;
    }

    public void setCmtlist(List<T> cmtlist) {
        this.cmtlist = cmtlist;
    }
}
