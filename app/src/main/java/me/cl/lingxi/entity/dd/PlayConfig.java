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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setString(List<String> line) {
        this.line = line;
    }

    public List<String> getString() {
        return this.line;
    }

    public void setSourcelib(String sourcelib) {
        this.sourcelib = sourcelib;
    }

    public String getSourcelib() {
        return this.sourcelib;
    }
}
