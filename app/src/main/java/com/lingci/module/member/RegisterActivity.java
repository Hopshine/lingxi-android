package com.lingci.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.module.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.do_password)
    EditText mDoPassword;
    @BindView(R.id.phone)
    EditText mPhone;

    private CustomProgressDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mToolbar.setTitle(R.string.title_bar_reg);
    }

    public void goRegister(View view) {
        registerProgress = new CustomProgressDialog(this, R.string.dialog_loading_reg, R.drawable.frame_loadin);
        String uName = mUsername.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            Toast.makeText(this, R.string.toast_reg_null, Toast.LENGTH_SHORT).show();
        } else {
            if (uPwd.equals(uDoPwd)) {
                if (uPhone.length() == 11) {
                    if (isMobileNum(uPhone)) {
                        postRegister(uName, uPwd, uPhone);
                    }
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.toast_phone_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.toast_again_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 验证手机
     *
     * @param mobiles 手机好
     * @return 是或否
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     *
     * @param userName 用户名
     * @param userPwd  秘密
     * @param phone    手机
     */
    public void postRegister(String userName, String userPwd, String phone) {
        registerProgress.show();
        OkHttpUtils.post()
                .url(Api.Url + "/register")
                .addParams("username", userName)
                .addParams("password", userPwd)
                .addParams("phone", phone)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        registerProgress.dismiss();
                        Log.d(TAG, "onError: " + id);
                        Toast.makeText(RegisterActivity.this, R.string.toast_reg_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        registerProgress.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            int tag = json.getInt("ret");
                            switch (tag) {
                                case 0:
                                    Toast.makeText(RegisterActivity.this, R.string.toast_reg_ok, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this,
                                            LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 2:
                                    Toast.makeText(RegisterActivity.this, R.string.toast_uname_being, Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(RegisterActivity.this, R.string.toast_phone_being, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
