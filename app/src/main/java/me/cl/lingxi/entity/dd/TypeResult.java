package me.cl.lingxi.entity.dd;

import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 类型结果
 * version: 1.0
 */
public class TypeResult {

    private int code;

    private List<ArcType> result;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setResult(List<ArcType> result) {
        this.result = result;
    }

    public List<ArcType> getResult() {
        return this.result;
    }
}
