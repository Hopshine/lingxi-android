package com.lingci.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lingci.entity.Result;

import java.lang.reflect.Type;

/**
 * Gson工具类
 * Created by bafsj on 17/3/31.
 */
public class GsonUtil {

    public interface GsonResult<T> {
        void onTrue(Result<T> result);
        void onErr(Result<Object> result, Exception e);
    }

    private Gson gson = new Gson();
    private String jsonStr = new String();
    private Type jsonType;

    public static GsonUtil build() {
        return new GsonUtil();
    }

    public GsonUtil setJson(String json) {
        jsonStr = json;
        return this;
    }

    public GsonUtil setType(Type type) {
        jsonType = type;
        return this;
    }

    public <T> GsonUtil setListener(GsonResult<T> listener) {
        try {
            Result<T> result = gson.fromJson(jsonStr, jsonType);
            listener.onTrue(result);
        } catch (Exception e) {
            Type type = new TypeToken<Result<Object>>() {
            }.getType();
            Result<Object> result = gson.fromJson(jsonStr, type);
            listener.onErr(result, e);
        }
        return this;
    }

    public static <T> void fromJson(String json, Type type, GsonResult<T> listener) {
        Gson gson = new Gson();
        try {
            Result<T> result = gson.fromJson(json, type);
            listener.onTrue(result);
        } catch (Exception e) {
            Type jsonType = new TypeToken<Result<Object>>() {
            }.getType();
            Result<Object> result = gson.fromJson(json, jsonType);
            listener.onErr(result, e);
        }
    }
}