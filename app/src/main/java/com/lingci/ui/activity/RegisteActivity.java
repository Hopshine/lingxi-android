package com.lingci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.utils.DaHttpRequest;
import com.lingci.views.CustomProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisteActivity extends Activity {

	private EditText userName, userPwd, douserPwd, userphone;
	private TextView tv_top;
	private CustomProgressDialog registerProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registe);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		tv_top = (TextView) this.findViewById(R.id.tv_top);
		userName = (EditText) this.findViewById(R.id.username);
		userPwd = (EditText) this.findViewById(R.id.password);
		douserPwd = (EditText) this.findViewById(R.id.dopassword);
		userphone = (EditText) this.findViewById(R.id.phone);
		tv_top.setText(R.string.title_bar_reg);
	}

	public void goRegister(View view) {
		registerProgress = new CustomProgressDialog(this,R.string.dialog_loading_reg,R.anim.frame_loadin);
		String uname = userName.getText().toString();
		String upwd = userPwd.getText().toString();
		String udopwd = douserPwd.getText().toString();
		String uphone = userphone.getText().toString();
		if (uname.length() == 0 || upwd.length() == 0 || udopwd.length() == 0
				|| uphone.length() == 0) {
			Toast.makeText(this,R.string.toast_reg_null, Toast.LENGTH_SHORT).show();
		} else {
			if (upwd.equals(udopwd)) {
				if (uphone.length() == 11) {
					if(isMobileNum(uphone)){
						RegisteAsyncHttpPost(uname, upwd, uphone);
					}
					Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this,R.string.toast_phone_error,Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this,R.string.toast_again_error,Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 验证手机
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {

		Pattern p = Pattern.compile("1[34578]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 注册请求
	 * @param userName
	 * @param userPwd
	 * @param phone
	 */
	public void RegisteAsyncHttpPost(String userName, String userPwd,
			String phone) {
		String path = GlobalParame.URl + "/register";
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		params.put("username", userName);
		params.put("password", userPwd);
		params.put("phone", phone);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(RegisteActivity.this,R.string.toast_reg_error, Toast.LENGTH_SHORT).show();
//				Log.i("TAG", "请求失败：" + new String(arg2));
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				registerProgress.show();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("TAG", "请求成功：" + str);
				try {
					JSONObject json = new JSONObject(str);
					int tag = json.getInt("ret");
					registerProgress.dismiss();
					switch (tag) {
					case 0:
						Toast.makeText(RegisteActivity.this,R.string.toast_reg_ok, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(RegisteActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
						break;
					case 2:
						Toast.makeText(RegisteActivity.this,R.string.toast_uname_being, Toast.LENGTH_SHORT).show();
						break;
					case 3:
						Toast.makeText(RegisteActivity.this,R.string.toast_phone_being, Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registe, menu);
		return true;
	}

}
