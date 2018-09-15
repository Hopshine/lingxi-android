package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 类型番剧
 * version: 1.0
 */
public class TypeArc {

    private String id;

    private String typename;

    private String suoluetudizhi;

    private String channeltype;

    private String biaoqian;

    private String writer;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTypename() {
        return this.typename;
    }

    public void setSuoluetudizhi(String suoluetudizhi) {
        this.suoluetudizhi = suoluetudizhi;
    }

    public String getSuoluetudizhi() {
        return this.suoluetudizhi;
    }

    public void setChanneltype(String channeltype) {
        this.channeltype = channeltype;
    }

    public String getChanneltype() {
        return this.channeltype;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    public String getBiaoqian() {
        return this.biaoqian;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return this.writer;
    }
}
