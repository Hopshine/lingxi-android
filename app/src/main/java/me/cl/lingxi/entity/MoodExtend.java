package me.cl.lingxi.entity;

import java.util.List;

public class MoodExtend {

    private int page;
    private int count;
    private int totalnum;
    private List<Mood> minifeedlist;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public List<Mood> getMinifeedlist() {
        return minifeedlist;
    }

    public void setMinifeedlist(List<Mood> minifeedlist) {
        this.minifeedlist = minifeedlist;
    }
}
