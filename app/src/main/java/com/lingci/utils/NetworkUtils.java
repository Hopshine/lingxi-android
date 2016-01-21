package com.lingci.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
/**
 * 网络请求类
 * @author wangqinghua
 *
 */
public class NetworkUtils {
	
	
    /**
     * 是否有可用的网络连接.
     *
     * @param context
     * @return
     */
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String action = ConnectivityManager.CONNECTIVITY_ACTION;
        if (cm.getActiveNetworkInfo() != null) {
            return cm.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 获取wifi状态
     *
     * @param context
     */
    static public State getWifiState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //wifi
        State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifi;
    }

    /**
     * 获取移动网络状态
     *
     * @param context
     * @return
     */
    static public State getMobileState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //mobile 3G Data Network
        State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        return mobile;
    }

    /**
     * 打开系统的网络设置页面
     *
     * @param activity
     */
    static public void openNetworkSettings(Activity activity) {
        activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }


    /**
     * 判断当前的网络是否连接
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable_(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isAvailable()){
                return true;
        }
        return false;
    }

    
    
    
    /**
     * 判断是否能连上网络
     * @param context
     */
    public static void checkNet(final Context context) {
    	
    	new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				// TODO Auto-generated method stub
				HttpURLConnection urlCon = null;
				String netResponse = "";
				String urlStr = params[0];
				java.net.URL url;
				try {
					url = new java.net.URL(urlStr);
					urlCon = (HttpURLConnection) url.openConnection();
					urlCon.setRequestMethod("GET");
					urlCon.setConnectTimeout(5000);
					InputStream is = urlCon.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					netResponse = response.toString();
					if (urlCon != null) {
						urlCon.disconnect();
					}
					if (is != null) {
						is.close();
					}
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				if (netResponse.indexOf("百度一下") > -1 || netResponse.indexOf("认证成功") > -1) {
					return true;
				} else {
					return false;
				}

			}

			protected void onPostExecute(Boolean result) {
				if (!result) {
					Toast.makeText(context,"网络连接过慢或不可用，请尝试手动切换网络.",Toast.LENGTH_SHORT).show();
				}
			};
		}.execute("http://wwww.baidu.com");
        }

    

}
