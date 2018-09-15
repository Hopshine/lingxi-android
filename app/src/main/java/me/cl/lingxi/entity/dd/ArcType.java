package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 番剧分类
 * version: 1.0
 */
public class ArcType {

//    {
//            "typeid": "3917",
//            "typename": "2018年10月",
//            "suoluetudizhi": "http://www.dilidili.wang/uploads/allimg/180731/290_2154569801.jpg",
//            "tempindex": "{style}/list_riqitongyong.htm",
//            "duoshuoid": "%2018年10月%",
//            "banben": "",
//            "istype": "riqi"
//    }

    private String typeid;

    private String typename;

    private String suoluetudizhi;

    private String tempindex;

    private String duoshuoid;

    private String banben;

    private String istype;

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

    public void setSuoluetudizhi(String suoluetudizhi) {
        this.suoluetudizhi = suoluetudizhi;
    }

    public String getSuoluetudizhi() {
        return this.suoluetudizhi;
    }

    public void setTempindex(String tempindex) {
        this.tempindex = tempindex;
    }

    public String getTempindex() {
        return this.tempindex;
    }

    public void setDuoshuoid(String duoshuoid) {
        this.duoshuoid = duoshuoid;
    }

    public String getDuoshuoid() {
        return this.duoshuoid;
    }

    public void setBanben(String banben) {
        this.banben = banben;
    }

    public String getBanben() {
        return this.banben;
    }

    public void setIstype(String istype) {
        this.istype = istype;
    }

    public String getIstype() {
        return this.istype;
    }

}
