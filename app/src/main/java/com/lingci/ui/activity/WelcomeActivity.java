package com.lingci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.constants.PreferencesManager;
import com.lingci.globals.BaseApplication;
import com.lingci.utils.DaHttpRequest;
import com.lingci.utils.MoeToast;
import com.lingci.utils.NetworkUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class WelcomeActivity extends Activity {
	
	private int runCount;
	private boolean islogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		GlobalParame.main = this;
		int x = (int) (Math.random() * 6) + 1;
		if(x==5){
//			ToastUtil.showSingleton(this, "然而一切都指向大结局！");
			MoeToast.makeText(this, "然而一切都指向大结局！");
		}
		islogin = PreferencesManager.getInstance().getBoolean("islogin", false);
		boolean isNetwork =  NetworkUtils.isNetworkAvailable(this);
		String uname = PreferencesManager.getInstance().getString("username", "");
		if(isNetwork && islogin){
//			getUnReadAsyncHttpPost(uname);
			getImTokenAsyncHttpPost();
//			goHome();
	    }else{
	    	final Handler handler = new Handler();
		    runCount = 0;
		    Runnable runnable = new Runnable() {
	
		      @Override
		      public void run() {
		        // TODO Auto-generated method stub
		        if (runCount == 1) {
		    		if (islogin) {
		    			goHome();
		    		}else{
		    			goLogin();
		    		}
		          handler.removeCallbacks(this);
		        }
		        handler.postDelayed(this, 150);
		        runCount++;
		      }
		    };
		    handler.postDelayed(runnable, 2000);
	    }
	}

	public void getImTokenAsyncHttpPost() {
		String path = GlobalParame.URl + "/getImToken";
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
								  Throwable arg3) {
				// TODO Auto-generated method stub
//				Log.i("TAG", "请求失败：" + new String(arg2));
				goHome();
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("TAG", "请求成功：" + str);
				try {
					JSONObject json = new JSONObject(str);
					int tag = json.getInt("ret");
					String token = json.getString("token");
					switch (tag) {
						case 0:
							connect(token);
							break;
						default:
							goHome();
							break;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/** 获取未读条数 */
	public void getUnReadAsyncHttpPost(String uname) {
		String path = GlobalParame.URl + "/unSeeNum";
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		params.put("uname", uname);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
//				Log.i("TAG", "请求失败：" + new String(arg2));
				goHome();
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("TAG", "请求成功：" + str);
				try {
					JSONObject json = new JSONObject(str);
					int tag = json.getInt("ret");
					int num = json.getInt("data");
					switch (tag) {
					case 0:
						Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
						intent.addFlags(num);
						startActivity(intent);
						finish();
						break;
					case 1:
						goHome();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void goHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void goLogin() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	/**
	 * 建立与融云服务器的连接
	 *
	 * @param token
	 */
	private void connect(String token) {

		if (getApplicationInfo().packageName.equals(BaseApplication.getCurProcessName(getApplicationContext()))) {

			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(token, new RongIMClient.ConnectCallback() {

				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
				 */
				@Override
				public void onTokenIncorrect() {

					Log.d("WelcomeActivity", "--onTokenIncorrect");
				}

				/**
				 * 连接融云成功
				 * @param userid 当前 token
				 */
				@Override
				public void onSuccess(String userid) {

					Log.d("WelcomeActivity", "--onSuccess" + userid);
					goHome();
				}

				/**
				 * 连接融云失败
				 * @param errorCode 错误码，可到官网 查看错误码对应的注释
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

					Log.d("WelcomeActivity", "--onError" + errorCode);
				}
			});
		}
	}
}
