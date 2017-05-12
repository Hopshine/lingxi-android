package com.lingci.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lingci.R;
import com.lingci.common.config.Api;
import com.lingci.common.config.App;
import com.lingci.common.config.Constants;
import com.lingci.common.config.JsonCallback;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.view.MoeToast;
import com.lingci.entity.Result;
import com.lingci.entity.User;
import com.lingci.entity.UserBean;
import com.lingci.module.main.MainActivity;
import com.lingci.module.member.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.reflect.Type;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
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
        Constants.main = this;
        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) {
            MoeToast.makeText(this, "然而一切都指向大结局！");
        }
        isLogin = SPUtils.getInstance(WelcomeActivity.this).getBoolean("islogin", false);
        String uname = SPUtils.getInstance(WelcomeActivity.this).getString("username", "");
        String im_token = SPUtils.getInstance(WelcomeActivity.this).getString("im_token", "");
        if (isLogin) {
            if (im_token == null || im_token.length() == 0) {
                Utils.toastShow(this, "登录过期，请重新登陆");
                goLogin();
            } else {
                connect(im_token);
            }

//			getUnRead(uname);
//			connect("YBoE5TbZRoxjyANO7PhPZmxxQZ3l7/ZMl8kUNMlzTysfsGEocvBjJ6uALHKWwrhkiemqgLCrNkE=");
//			goHome();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLogin) {
                        goHome(0);
                    } else {
                        goLogin();
                    }
                }
            }, 2000);
        }
    }

    public void getImUser() {
        String lctoken = Utils.getMetaValue(this, "LINGCI_APP_KEY");
        OkHttpUtils.post()
                .url(Api.Url + "/getImUser")
                .addParams("lctoken", lctoken)
                .build()
                .execute(new JsonCallback<Result<UserBean<User>>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        goHome(0);
                    }

                    @Override
                    public void onResponse(Result<UserBean<User>> response, int id) {
                        for (User user: response.getData().getUserlist()){
                            UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.Url + user.getUrl()));
                            if (!Constants.uidList.contains(String.valueOf(user.getUid()))) {
                                Constants.uidList.add(String.valueOf(user.getUid()));
                                Constants.userList.add(userInfo);
                            }
                        }
                    }
                });
    }

    /**
     * 获取未读条数
     */
    public void getUnRead(String uname) {
        OkHttpUtils.post()
                .url(Api.Url + "/unSeeNum")
                .addParams("uname", uname)
                .build()
                .execute(new JsonCallback<Result<Integer>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        goHome(0);
                    }

                    @Override
                    public void onResponse(Result<Integer> response, int id) {
                        goHome(response.getData());
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
                    String imUserStr = SPUtils.getInstance(WelcomeActivity.this).getString("im_User", null);
                    if (imUserStr == null || imUserStr.length() == 0) {
                        getImUser();
                    } else {
                        Type type = new TypeToken<Result<UserBean<User>>>() {}.getType();
                        com.lingci.entity.Result<UserBean<User>> userResult = GsonUtil.toObject(imUserStr, type);
                        for (User user: userResult.getData().getUserlist()){
                            UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.Url + user.getUrl()));
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
