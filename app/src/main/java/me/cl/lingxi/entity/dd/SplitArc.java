package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 拼合番剧，需要融合两个id {@link SortArc},{@link TypeArc}
 * version: 1.0
 */
public class SplitArc {

    /**
     * 类型番剧id
     */
    private String id;

    /**
     * 榜单番剧id
     */
    private String typeid;

    /**
     * 番剧名称
     */
    private String typename;

    /**
     * 番剧缩略图
     */
    private String suoluetudizhi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getSuoluetudizhi() {
        return suoluetudizhi;
    }

    public void setSuoluetudizhi(String suoluetudizhi) {
        this.suoluetudizhi = suoluetudizhi;
    }
}
