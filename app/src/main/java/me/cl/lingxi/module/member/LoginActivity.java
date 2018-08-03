package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.result.Result;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.library.view.MoeToast;
import okhttp3.Call;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    AppCompatEditText mUsername;
    @BindView(R.id.password)
    AppCompatEditText mPassword;

    private long mExitTime = 0;
    private LoadingDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, R.string.title_bar_login, false, 0, null);
        loginProgress = new LoadingDialog(this, R.string.dialog_loading_login);

        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) MoeToast.makeText(this, "从哪里来到哪里去？你明白吗？");

        String saveName = SPUtil.build().getString(Constants.USER_NAME);
        mUsername.setText(saveName);
        mUsername.setSelection(saveName.length());
    }

    public void login(View view) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Utils.showToast(LoginActivity.this, R.string.toast_login_null);
            return;
        }
        postLogin(username, password);
    }

    // 登录请求
    public void postLogin(final String userName, String userPwd) {
        loginProgress.show();
        OkUtil.post()
                .url(Api.userLogin)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .execute(new ResultCallback<Result<UserInfo>>() {
                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                UserInfo user = response.getData();
                                SPUtil.build().putBoolean(Constants.BEEN_LOGIN, true);
                                SPUtil.build().putString(Constants.USER_ID, user.getId());
                                SPUtil.build().putString(Constants.USER_NAME, user.getUsername());
                                goHome();
                                break;
                            default:
                                loginProgress.dismiss();
                                Utils.showToast(LoginActivity.this, R.string.toast_pwd_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        loginProgress.dismiss();
                        Utils.showToast(LoginActivity.this, R.string.toast_login_error);
                    }

                    @Override
                    public void onFinish() {
                        loginProgress.dismiss();
                        Utils.showToast(LoginActivity.this, R.string.toast_login_error);
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

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void updatePwd(View view) {
        Intent intent = new Intent(LoginActivity.this, ResetPwdActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        if (loginProgress.isShowing()) {
            loginProgress.dismiss();
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constants.UNREAD_NUM, 0);
        startActivity(intent);
        finish();
    }

}
