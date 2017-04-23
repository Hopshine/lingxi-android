package com.lingci.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lingci.entity.Result;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Gson工具类
 * Created by bafsj on 17/3/31.
 */
public class GsonUtil {

    public interface GsonResult<T> {
        void onTrue(Result<T> result);
        void onErr(Result<Object> result, Exception e);
    }

    private static Gson gson = new Gson();
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

    /**
     * 解析json
     * @param json json字符串
     * @param clazz class
     * @return 对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 解析json数组
     * @param json json字符串
     * @param clazz 示例 T[].class
     * @return 集合
     */
    public static <T> List<T> toList(String json, Class<T[]> clazz) {
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    /**
     * 将Object转为json
     * @param src Object
     * @return json字符串
     */
    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}