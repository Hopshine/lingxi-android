package me.cl.lingxi.entity.dd;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 一周番剧本体
 * version: 1.0
 */
public class WeekArc {

    private String id;

    private String typename;

    private String upnumber;

    private String pubdate;

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

    public void setUpnumber(String upnumber) {
        this.upnumber = upnumber;
    }

    public String getUpnumber() {
        return this.upnumber;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPubdate() {
        return this.pubdate;
    }

}
