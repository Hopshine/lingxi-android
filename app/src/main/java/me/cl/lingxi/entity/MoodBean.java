package me.cl.lingxi.entity;

import java.util.List;

public class MoodBean<T> {

    private int totalnum;
    private List<T> minifeedlist;

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public List<T> getMinifeedlist() {
        return minifeedlist;
    }

    public void setMinifeedlist(List<T> minifeedlist) {
        this.minifeedlist = minifeedlist;
    }
}
