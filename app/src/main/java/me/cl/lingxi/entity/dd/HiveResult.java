package me.cl.lingxi.entity.dd;

import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 番剧剧集
 * version: 1.0
 */
public class HiveResult {

    private int code;

    private int count;

    private List<Hive> list;

    private Hive lastitem;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setList(List<Hive> list) {
        this.list = list;
    }

    public List<Hive> getList() {
        return this.list;
    }

    public void setLastitem(Hive lastitem) {
        this.lastitem = lastitem;
    }

    public Hive getLastitem() {
        return this.lastitem;
    }
}
