package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.App;
import me.cl.lingxi.common.config.JsonCallback;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.view.CustomProgressDialog;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.main.MainActivity;
import okhttp3.Call;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;

    private String saveuname;
    private long mExitTime = 0;
    private CustomProgressDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        String savename = SPUtils.getInstance(this).getString("username", "");
        mToolbar.setTitle(R.string.title_bar_login);

        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) MoeToast.makeText(this, "从哪里来到哪里去？你明白吗？");

        mUsername.setText(savename);
        mUsername.setSelection(savename.length());
    }

    public void login(View view) {
        String uname = mUsername.getText().toString().trim();
        String upwd = mPassword.getText().toString().trim();
        loginProgress = new CustomProgressDialog(this, R.string.dialog_loading_lg);
        if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(upwd)) {
            postLogin(uname, upwd);
        } else {
            Toast.makeText(LoginActivity.this, R.string.toast_login_null, Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void updatePwd(View view) {
        Intent intent = new Intent(LoginActivity.this, UpdatePwdActivity.class);
        startActivity(intent);
    }

    // 登录请求
    public void postLogin(final String userName, String userPwd) {
        loginProgress.show();
        saveuname = userName;
        OkHttpUtils.post()
                .url(Api.Url + "/Login")
                .addParams("username", userName)
                .addParams("password", userPwd)
                .build()
                .execute(new JsonCallback<Result<User>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loginProgress.dismiss();
                        Toast.makeText(LoginActivity.this, R.string.toast_login_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Result<User> response, int id) {
                        loginProgress.dismiss();
                        int tag = response.getRet();
                        switch (tag) {
                            case 0:
                                User user = response.getData();
                                String im_token = user.getIm_token();
                                SPUtils.getInstance(LoginActivity.this).putBoolean("islogin", true);
                                SPUtils.getInstance(LoginActivity.this).putInt("uid", user.getUid());
                                SPUtils.getInstance(LoginActivity.this).putString("username", user.getUname());
                                SPUtils.getInstance(LoginActivity.this).putString("im_token", im_token);
                                if (TextUtils.isEmpty(im_token))
                                    goHome();
                                else
                                    connect(im_token);
                                break;
                            case 2:
                                Toast.makeText(LoginActivity.this, R.string.toast_pwd_error, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, R.string.toast_again_exit);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {
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
                    loginProgress.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.toast_login_ok, Toast.LENGTH_SHORT).show();
                    goHome();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("WelcomeActivity", "--onError" + errorCode);
                    goHome();
                }
            });
        }
    }

    private void goHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
