package me.cl.lingxi.entity.dd;

import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 播放设置
 * version: 1.0
 */
public class PlayConfig {

    private String id;

    private List<String> line;

    private String sourcelib;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLine() {
        return line;
    }

    public void setLine(List<String> line) {
        this.line = line;
    }

    public String getSourcelib() {
        return sourcelib;
    }

    public void setSourcelib(String sourcelib) {
        this.sourcelib = sourcelib;
    }
}
