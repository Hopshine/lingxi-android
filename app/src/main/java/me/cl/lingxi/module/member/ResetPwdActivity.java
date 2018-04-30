package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.UserInfo;
import okhttp3.Call;

public class ResetPwdActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.do_password)
    EditText mDoPassword;

    private LoadingDialog updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, R.string.title_bar_reset_pwd, true, 0, null);
    }

    public void goUpdatePwd(View view) {
        updateProgress = new LoadingDialog(this, R.string.dialog_loading_reset_wd);
        String uName = mUsername.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            Utils.toastShow(this, R.string.toast_reg_null);
        }
        if (uPhone.length() != 11) {
            Utils.toastShow(this, R.string.toast_phone_format_error);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            Utils.toastShow(this, R.string.toast_again_error);
            return;
        }
        postUpdatePwd(uName, uPwd, uPhone);
    }

    public void postUpdatePwd(String userName, String userPwd, String phone) {
        OkUtil.post()
                .url(Api.resetPassword)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        updateProgress.dismiss();
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                Utils.toastShow(ResetPwdActivity.this, R.string.toast_reset_ped_success);
                                onBackPressed();
                                break;
                            case "00104":
                                Utils.toastShow(ResetPwdActivity.this, R.string.toast_reset_pwd_user);
                                break;
                            default:
                                Utils.toastShow(ResetPwdActivity.this, R.string.toast_reset_pwd_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        updateProgress.dismiss();
                        Utils.toastShow(ResetPwdActivity.this, R.string.toast_reset_pwd_error);
                    }

                    @Override
                    public void onFinish() {
                        updateProgress.dismiss();
                        Utils.toastShow(ResetPwdActivity.this, R.string.toast_reset_pwd_error);
                    }
                });
    }

}
