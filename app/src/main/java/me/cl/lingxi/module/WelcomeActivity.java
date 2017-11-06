package me.cl.lingxi.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Type;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Aplication;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.entity.UserExtend;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.module.member.LoginActivity;

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
        isLogin = SPUtils.getInstance(WelcomeActivity.this).getBoolean("islogin", false);
        final String im_token = SPUtils.getInstance(WelcomeActivity.this).getString("im_token", "");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    if (TextUtils.isEmpty(im_token)) {
                        Utils.toastShow(WelcomeActivity.this, "登录过期，请重新登录");
                        goLogin();
                    } else {
                        connect(im_token);
                    }
                } else {
                    goLogin();
                }
            }
        }, 1500);
    }

    public void getImUser() {
        String lctoken = Utils.getMetaValue(this, "LINGCI_APP_KEY");
        OkGo.<Result<UserExtend>>post(Api.imUser)
                .params("lctoken", lctoken)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<UserExtend>>() {
                    @Override
                    public void onSuccess(Response<Result<UserExtend>> response) {
                        for (User user: response.body().getData().getUserlist()){
                            UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.baseUrl + user.getUrl()));
                            if (!Constants.uidList.contains(String.valueOf(user.getUid()))) {
                                Constants.uidList.add(String.valueOf(user.getUid()));
                                Constants.userList.add(userInfo);
                            }
                        }
                        goHome(0);
                    }

                    @Override
                    public void onError(Response<Result<UserExtend>> response) {
                        goHome(0);
                    }
                });
    }

    /**
     * 获取未读条数
     */
    public void getUnRead(String uname) {
        OkGo.<Result<Integer>>post(Api.unSeeNum)
                .params("uname", uname)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Response<Result<Integer>> response) {
                        goHome(response.body().getData());
                    }

                    @Override
                    public void onError(Response<Result<Integer>> response) {
                        goHome(0);
                    }
                });
    }

    private void goHome(int num) {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.addFlags(num);
        startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token Token
     */
    private void connect(final String token) {
        if (getApplicationInfo().packageName.equals(Aplication.getCurProcessName(getApplicationContext()))) {
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
                    String imUserStr = SPUtils.getInstance(WelcomeActivity.this).getString("im_User", null);
                    if (imUserStr == null || imUserStr.length() == 0) {
                        getImUser();
                    } else {
                        Type type = new TypeToken<Result<UserExtend>>() {}.getType();
                        me.cl.lingxi.entity.Result<UserExtend> userResult = GsonUtil.toObject(imUserStr, type);
                        for (User user: userResult.getData().getUserlist()){
                            UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.baseUrl + user.getUrl()));
                            if (!Constants.uidList.contains(String.valueOf(user.getUid()))) {
                                Constants.uidList.add(String.valueOf(user.getUid()));
                                Constants.userList.add(userInfo);
                            }
                        }
                        goHome(0);
                    }
                }


                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("WelcomeActivity", "--onError" + errorCode);
                    goHome(0);
                }
            });
        }
    }
}
