package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 剧集本体
 * version: 1.0
 */
public class Hive {

    private String id;

    private String writer;

    private String click;

    private String bbs_tid;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return this.writer;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getClick() {
        return this.click;
    }

    public void setBbs_tid(String bbs_tid) {
        this.bbs_tid = bbs_tid;
    }

    public String getBbs_tid() {
        return this.bbs_tid;
    }
}
