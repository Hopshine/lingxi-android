package me.cl.lingxi.common.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import me.cl.lingxi.entity.Result;
import okhttp3.Response;

/**
 * JsonCallback
 */
public abstract class JsonCallback<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        // 得到类的泛型，包括了泛型参数
        Type genType = getClass().getGenericSuperclass();
        // 从上述的类中取出真实的泛型参数数组
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        // 取出第一个
        Type type = params[0];
        // 保证上面我解析到的type泛型，仍然还具有一层参数化的泛型，也就是两层泛型
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        // 此时rawType为class
        Type rawType = ((ParameterizedType) type).getRawType();
        // 获取最终内部泛型
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        // 以Gson解析，根据泛型解析返回对象
        JsonReader jsonReader = new JsonReader(response.body().charStream());
        if (rawType == Result.class) {
            Result result = new Gson().fromJson(jsonReader, type);
            response.close();
            return (T) result;
        } else {
            response.close();
            throw  new IllegalStateException("基类错误无法解析!");
        }
    }
}
