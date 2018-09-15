package me.cl.lingxi.entity.dd;

import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 一周番剧
 * version: 1.0
 */
public class WeekResult {

    private List<WeekArc> one;
    private List<WeekArc> tow;

    public void setOne(List<WeekArc> one) {
        this.one = one;
    }

    public List<WeekArc> getOne() {
        return this.one;
    }

    public void setTow(List<WeekArc> tow) {
        this.tow = tow;
    }

    public List<WeekArc> getTow() {
        return this.tow;
    }

}
