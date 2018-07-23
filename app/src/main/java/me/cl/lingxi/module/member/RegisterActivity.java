package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.UserInfo;
import okhttp3.Call;

/**
 * 用户注册
 */
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

    private LoadingDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, R.string.title_bar_reg, true, 0, null);
    }

    public void goRegister(View view) {
        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);
        String uName = mUsername.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            Utils.showToast(this, R.string.toast_reg_null);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            Utils.showToast(this, R.string.toast_again_error);
            return;
        }
        if (uPhone.length() != 11) {
            Utils.showToast(this, R.string.toast_phone_format_error);
            return;
        }
        if (!isMobileNum(uPhone)) {
            Utils.showToast(this, R.string.toast_phone_format_error);
            return;
        }
        postRegister(uName, uPwd, uPhone);
    }

    /**
     * 验证手机
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     */
    public void postRegister(String userName, String userPwd, String phone) {
        registerProgress.show();
        OkUtil.post()
                .url(Api.userRegister)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        registerProgress.dismiss();
                        String code = response.getCode();
                        switch (code) {
                            case "00000":
                                Utils.showToast(RegisterActivity.this, R.string.toast_reg_success);
                                UserInfo user = response.getData();
                                SPUtil.build().putString(Constants.USER_NAME, user.getUsername());
                                onBackPressed();
                                break;
                            case "00105":
                                Utils.showToast(RegisterActivity.this, R.string.toast_phone_being);
                                break;
                            case "00106":
                                Utils.showToast(RegisterActivity.this, R.string.toast_username_being);
                                break;
                            default:
                                Utils.showToast(RegisterActivity.this, R.string.toast_reg_error);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        registerProgress.dismiss();
                        Utils.showToast(RegisterActivity.this, R.string.toast_reg_error);
                    }

                    @Override
                    public void onFinish() {
                        registerProgress.dismiss();
                        Utils.showToast(RegisterActivity.this, R.string.toast_reg_error);
                    }
                });
    }

}
