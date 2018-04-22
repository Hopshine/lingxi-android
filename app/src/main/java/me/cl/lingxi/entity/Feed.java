package me.cl.lingxi.entity;

import java.io.Serializable;
import java.util.List;

public class Feed implements Serializable {

    private int lcid;
    private int uid;
    private String uname;
    private String url;
    private int im_ability;
    private String lc_info;
    private String pl_time;
    private int viewnum;
    private int likenum;
    private int cmtnum;
    private List<String> photos;
    private int islike;
    private List<Like> likelist;

    public int getLcid() {
        return lcid;
    }

    public void setLcid(int lcid) {
        this.lcid = lcid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isIm_ability() {
        return im_ability == 0;
    }

    public void setIm_ability(boolean im_ability) {
        this.im_ability = im_ability ? 0 : 1;
    }

    public String getLc_info() {
        return lc_info;
    }

    public void setLc_info(String lc_info) {
        this.lc_info = lc_info;
    }

    public String getPl_time() {
        return pl_time;
    }

    public void setPl_time(String pl_time) {
        this.pl_time = pl_time;
    }

    public int getViewnum() {
        return viewnum;
    }

    public void setViewnum(int viewnum) {
        this.viewnum = viewnum;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public int getCmtnum() {
        return cmtnum;
    }

    public void setCmtnum(int cmtnum) {
        this.cmtnum = cmtnum;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public boolean islike() {
        return islike == 1;
    }

    public void setIslike(boolean islike) {
        this.islike = islike ? 1 : 0;
    }

    public List<Like> getLikelist() {
        return likelist;
    }

    public void setLikelist(List<Like> likelist) {
        this.likelist = likelist;
    }
}
