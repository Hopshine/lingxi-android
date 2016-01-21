package com.lingci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.constants.PreferencesManager;
import com.lingci.utils.DaHttpRequest;
import com.lingci.utils.MoeToast;
import com.lingci.views.CustomProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

	private TextView tv_top;
	private EditText userName,userPwd;
	private String saveuname;
	private long mExitTime = 0;
	private CustomProgressDialog loginProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		tv_top = (TextView) this.findViewById(R.id.tv_top);
		userName = (EditText) this.findViewById(R.id.username);
		userPwd = (EditText) this.findViewById(R.id.password);
		String savename = PreferencesManager.getInstance().getString("username", "");
		tv_top.setText(R.string.title_bar_login);

		int x = (int) (Math.random() * 6) + 1;
		if(x==5){
//			ToastUtil.showSingleton(this, "从哪里来到哪里去？你明白吗？");
			MoeToast.makeText(this, "从哪里来到哪里去？你明白吗？");
		}
		userName.setText(savename);
		userName.setSelection(savename.length());
	}

	public void login(View view) {
		String uname = userName.getText().toString();
		String upwd = userPwd.getText().toString();
		loginProgress = new CustomProgressDialog(this, R.string.dialog_loading_lg,R.anim.frame_loadin);
		if(uname!=null&&upwd!=null&&uname.length()!=0&&upwd.length()!=0){
			loginAsyncHttpPost(uname, upwd);
		}else{
			Toast.makeText(LoginActivity.this, R.string.toast_login_null, Toast.LENGTH_SHORT).show();
		}
	}

	public void register(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisteActivity.class);
		startActivity(intent);
	}
	
	public void updatePwd(View view) {
		Intent intent = new Intent(LoginActivity.this, UpdatePwdActivity.class);
		startActivity(intent);
	}

	public void loginAsyncHttpPost(String userName, String userPwd) {
		String path = GlobalParame.URl + "/Login";
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		params.put("username", userName);
		params.put("password", userPwd);
		saveuname = userName;
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				loginProgress.dismiss();
				Toast.makeText(LoginActivity.this,R.string.toast_login_error, Toast.LENGTH_SHORT).show();
//				Log.i("TAG", "请求失败：" + new String(arg2));
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				loginProgress.show();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("TAG", "请求成功：" + str);
				try {
					JSONObject json = new JSONObject(str);
					int tag = json.getInt("ret");
					loginProgress.dismiss();
					switch (tag) {
					case 0:
						PreferencesManager.getInstance().putBoolean("islogin", true);
						PreferencesManager.getInstance().putString("username", saveuname);
						Toast.makeText(LoginActivity.this,R.string.toast_login_ok, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
						break;
					case 2:
						Toast.makeText(LoginActivity.this,R.string.toast_pwd_error, Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
//				Toast.makeText(this,R.string.toast_again_exit, Toast.LENGTH_SHORT).show();
				MoeToast.makeText(this,R.string.toast_again_exit);
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
