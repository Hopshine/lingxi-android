package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 最近更新番剧
 * version: 1.0
 */
public class NewArc {

//    {
//            "typeid": "3317",
//            "typename": "前进吧!登山少女 第三季",
//            "writer": "10",
//            "litpic": "http://www.dilidili.wang/uploads/allimg/180904/290_0247345261.png",
//            "id": "70554",
//            "arcrank": "0"
//    }

    private String typeid;

    private String typename;

    private String writer;

    private String litpic;

    private String id;

    private String arcrank;

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getTypeid() {
        return this.typeid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTypename() {
        return this.typename;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriter() {
        return this.writer;
    }

    public void setLitpic(String litpic) {
        this.litpic = litpic;
    }

    public String getLitpic() {
        return this.litpic;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setArcrank(String arcrank) {
        this.arcrank = arcrank;
    }

    public String getArcrank() {
        return this.arcrank;
    }
}
