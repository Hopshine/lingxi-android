package com.lingci.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.module.BaseActivity;
import com.lingci.common.view.CustomProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class UpdatePwdActivity extends BaseActivity {

	private EditText userName, userPwd, doUserPwd, userPhone;
	private TextView tv_top;
	private CustomProgressDialog updateProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatepwd);
		init();
	}

	private void init() {
		tv_top = (TextView) this.findViewById(R.id.tv_top);
		userName = (EditText) this.findViewById(R.id.username);
		userPwd = (EditText) this.findViewById(R.id.password);
		doUserPwd = (EditText) this.findViewById(R.id.dopassword);
		userPhone = (EditText) this.findViewById(R.id.phone);
		tv_top.setText(R.string.title_bar_upwd);
	}

	public void goUpdatePwd(View view) {
		updateProgress = new CustomProgressDialog(this,R.string.dialog_loading_upwd,R.drawable.frame_loadin);
		String uName = userName.getText().toString().trim();
		String uPwd = userPwd.getText().toString().trim();
		String uDoPwd = doUserPwd.getText().toString().trim();
		String uPhone = userPhone.getText().toString().trim();
		if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
			Toast.makeText(this, R.string.toast_reg_null, Toast.LENGTH_SHORT).show();
		} else {
			if (uPwd.equals(uDoPwd)) {
				if (uPhone.length() == 11) {
					postUpdatePwd(uName, uPwd, uPhone);
				} else {
					Toast.makeText(this,R.string.toast_phone_error, Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this,R.string.toast_again_error, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void postUpdatePwd(String userName, String userPwd, String phone) {
		OkHttpUtils.post()
				.url(Api.Url + "/updatePwd")
				.addParams("username", userName)
				.addParams("password", userPwd)
				.addParams("phone", phone)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						updateProgress.dismiss();
						Log.d(TAG, "onError: " + id);
						Toast.makeText(UpdatePwdActivity.this,R.string.toast_upwd_error, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(String response, int id) {
						updateProgress.dismiss();
						Log.d(TAG, "onResponse: " + response);
						try {
							JSONObject json = new JSONObject(response);
							int tag = json.getInt("ret");
							switch (tag) {
								case 0:
									Toast.makeText(UpdatePwdActivity.this,R.string.toast_uped_ok, Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(UpdatePwdActivity.this, LoginActivity.class);
									startActivity(intent);
									finish();
									break;
								case 2:
									Toast.makeText(UpdatePwdActivity.this,R.string.toast_uname_error, Toast.LENGTH_SHORT).show();
									break;
								case 3:
									Toast.makeText(UpdatePwdActivity.this,R.string.toast_uphone_error, Toast.LENGTH_SHORT).show();
									break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

}
