package com.lingci.utils;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * 异步网络请求模板
 *
 * @author jing.wang
 * @version 1.0
 * @date 2015-3-29
 * @api http://loopj.com/android-async-http/doc/com/loopj/android/http/package-summary.html
 * 
 */
public final class DaHttpRequest {
    /**
     * 实例化对象
     */
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * 缓存Cookie
     */
    private PersistentCookieStore cookieStore;

    private Context context;


    /**初始化相关参数，设置超时时间*/
    static {
        client.setTimeout(10000);
    }

    public DaHttpRequest(Context c) {
        this.context = c;
        cookieStore = new PersistentCookieStore(c);
        client.setCookieStore(cookieStore);
    }

    /**
     * get请求
     *
     * @param url             服务器地址
     * @param params          请求参数
     * @param responseHandler 响应处理器
     */
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	if (!NetworkUtils.isNetworkAvailable(context)) {
			Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
        client.setEnableRedirects(true);//允许重定向
        client.get(url, params, responseHandler);
//        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * delete请求
     *
     * @param url             服务器地址
     * @param params          请求参数
     * @param responseHandler 响应处理器
     */
    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	if (!NetworkUtils.isNetworkAvailable(context)) {
			Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
        client.setEnableRedirects(true);//允许重定向
        client.delete(context, getAbsoluteUrl(url), null, params, responseHandler);
    }
    
    /**
     * post请求
     *
     * @param url             服务器地址
     * @param params          请求参数
     * @param responseHandler 响应处理器
     */
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	if (!NetworkUtils.isNetworkAvailable(context)) {
			Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
    	client.post(url, params, responseHandler);
//        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     *
     * @param url
     * @param responseHandler
     */
    public void post_crftoken(String url, RequestParams params,AsyncHttpResponseHandler responseHandler) {
    	if (!NetworkUtils.isNetworkAvailable(context)) {
			Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
			return;
		}
        client.post(getAbsoluteUrl(url), params,responseHandler);
    }

    /**
     * 获取url绝对路径
     *
     * @param relativeUrl
     * @return
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl.startsWith("http://")){
            Log.i("ml", ""+BaseURL.ROOT+relativeUrl);
        	return BaseURL.ROOT+relativeUrl;
        }
        Log.i("ml", "http://"+BaseURL.ROOT+relativeUrl);
        return  "http://"+BaseURL.ROOT+relativeUrl;
    }


    /**
     * 取消当前的网络请求
     *
     * @param c
     */
    public void cancelRequest(Context c) {
        client.cancelRequests(c, true);
    }
    
    /**
     * 清除cookie
     */
    public void clearCookies() {
    	cookieStore.clear();
    }

}
