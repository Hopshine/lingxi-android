package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 剧集信息
 * version: 1.0
 */
public class HiveInfo {

    private String id;

    private String typeid;

    private String writer;

    private String title;

    private String litpic;

    private String pubdate;

    private String keywords;

    private String description;

    private String click;

    private String bbs_tid;

    private HiveDetail detail;

    private HiveArc arctype;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getTypeid() {
        return this.typeid;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return this.writer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setLitpic(String litpic) {
        this.litpic = litpic;
    }

    public String getLitpic() {
        return this.litpic;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPubdate() {
        return this.pubdate;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
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

    public void setDetail(HiveDetail detail) {
        this.detail = detail;
    }

    public HiveDetail getDetail() {
        return this.detail;
    }

    public void setArctype(HiveArc arctype) {
        this.arctype = arctype;
    }

    public HiveArc getArctype() {
        return this.arctype;
    }
}
