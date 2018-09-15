package me.cl.lingxi.entity.dd;

import java.io.Serializable;
import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 番剧本体
 * version: 1.0
 */
public class ArcInfo implements Serializable {

    private String id;

    private String typename;

    private String typedir;

    private String suoluetudizhi;

    private String diqu;

    private String niandai;

    private String biaoqian;

    private String description;

    private String leixingtuijian;

    private String xiazaidizhi;

    private String bbs_tid;

    private String zhuangtai;

    private String duoji;

    private List<ArcQuarter> duoji_info;

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

    public void setTypedir(String typedir) {
        this.typedir = typedir;
    }

    public String getTypedir() {
        return this.typedir;
    }

    public void setSuoluetudizhi(String suoluetudizhi) {
        this.suoluetudizhi = suoluetudizhi;
    }

    public String getSuoluetudizhi() {
        return this.suoluetudizhi;
    }

    public void setDiqu(String diqu) {
        this.diqu = diqu;
    }

    public String getDiqu() {
        return this.diqu;
    }

    public void setNiandai(String niandai) {
        this.niandai = niandai;
    }

    public String getNiandai() {
        return this.niandai;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    public String getBiaoqian() {
        return this.biaoqian;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setLeixingtuijian(String leixingtuijian) {
        this.leixingtuijian = leixingtuijian;
    }

    public String getLeixingtuijian() {
        return this.leixingtuijian;
    }

    public void setXiazaidizhi(String xiazaidizhi) {
        this.xiazaidizhi = xiazaidizhi;
    }

    public String getXiazaidizhi() {
        return this.xiazaidizhi;
    }

    public void setBbs_tid(String bbs_tid) {
        this.bbs_tid = bbs_tid;
    }

    public String getBbs_tid() {
        return this.bbs_tid;
    }

    public void setZhuangtai(String zhuangtai) {
        this.zhuangtai = zhuangtai;
    }

    public String getZhuangtai() {
        return this.zhuangtai;
    }

    public void setDuoji(String duoji) {
        this.duoji = duoji;
    }

    public String getDuoji() {
        return this.duoji;
    }

    public void setDuoji_info(List<ArcQuarter> duoji_info) {
        this.duoji_info = duoji_info;
    }

    public List<ArcQuarter> getDuoji_info() {
        return this.duoji_info;
    }

}
