package me.cl.lingxi.module.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.module.member.LoginActivity;
import me.cl.lingxi.view.MoeToast;
import okhttp3.Call;

public class WelcomeActivity extends BaseActivity {

    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) {
            MoeToast.makeText(this, "然而一切都指向大结局！");
        }

        isLogin = SPUtil.build().getBoolean(Constants.BEEN_LOGIN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    getUnRead();
                } else {
                    goLogin();
                }
            }
        }, 1500);
    }

    /**
     * 获取未读条数
     */
    public void getUnRead() {
        String userId = SPUtil.build().getString(Constants.USER_ID);
        OkUtil.post()
                .url(Api.unreadComment)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        goHome("00000".equals(response.getCode()) ? response.getData() : 0);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        goHome(0);
                    }

                    @Override
                    public void onFinish() {
                        goHome(0);
                    }
                });
    }

    private void goHome(int num) {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.putExtra(Constants.UNREAD_NUM, num);
        startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
