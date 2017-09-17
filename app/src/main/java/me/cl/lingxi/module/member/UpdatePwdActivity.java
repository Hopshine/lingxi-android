package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.CustomProgressDialog;
import me.cl.lingxi.common.widget.JsonCallback;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;

public class UpdatePwdActivity extends BaseActivity {

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

    private CustomProgressDialog updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepwd);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mToolbar.setTitle(R.string.title_bar_upwd);
    }

    public void goUpdatePwd(View view) {
        updateProgress = new CustomProgressDialog(this, R.string.dialog_loading_upwd);
        String uName = mUsername.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            Utils.toastShow(this, R.string.toast_reg_null);
        } else {
            if (uPwd.equals(uDoPwd)) {
                if (uPhone.length() == 11) {
                    postUpdatePwd(uName, uPwd, uPhone);
                } else {
                    Utils.toastShow(this, R.string.toast_phone_error);
                }
            } else {
                Utils.toastShow(this, R.string.toast_again_error);
            }
        }
    }

    public void postUpdatePwd(String userName, String userPwd, String phone) {
        OkGo.<Result>post(Api.updatePwd)
                .params("username", userName)
                .params("password", userPwd)
                .params("phone", phone)
                .execute(new JsonCallback<Result>() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        int tag = response.body().getRet();
                        switch (tag) {
                            case 0:
                                Utils.toastShow(UpdatePwdActivity.this, R.string.toast_uped_ok);
                                Intent intent = new Intent(UpdatePwdActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case 2:
                                Utils.toastShow(UpdatePwdActivity.this, R.string.toast_uname_error);
                                break;
                            case 3:
                                Utils.toastShow(UpdatePwdActivity.this, R.string.toast_uphone_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        updateProgress.dismiss();
                        Utils.toastShow(UpdatePwdActivity.this, R.string.toast_upwd_error);
                    }
                });
    }

}
