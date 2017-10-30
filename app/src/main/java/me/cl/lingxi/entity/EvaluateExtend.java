package me.cl.lingxi.entity;

import java.util.List;

public class EvaluateExtend {

    private int totalnum;
    private List<Evaluate> cmtlist;

    private int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public List<Evaluate> getCmtlist() {
        return cmtlist;
    }

    public void setCmtlist(List<Evaluate> cmtlist) {
        this.cmtlist = cmtlist;
    }
}
