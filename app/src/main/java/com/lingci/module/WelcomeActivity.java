package com.lingci.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.BaseApplication;
import com.lingci.common.Constants;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.MoeToast;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.entity.Result;
import com.lingci.entity.User;
import com.lingci.entity.UserBean;
import com.lingci.module.main.MainActivity;
import com.lingci.module.member.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isLogin) {
                        goHome();
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
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        goHome();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        SPUtils.getInstance(WelcomeActivity.this).putString("im_User", response);
                        Type type = new TypeToken<Result<UserBean<User>>>() {}.getType();
                        GsonUtil.fromJson(response, type, new GsonUtil.GsonResult<UserBean<User>>() {

                            @Override
                            public void onTrue(Result<UserBean<User>> result) {
                                for (User user: result.getData().getUserlist()){
                                    UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.Url + user.getUrl()));
                                    if (!Constants.uidList.contains(String.valueOf(user.getUid()))) {
                                        Constants.uidList.add(String.valueOf(user.getUid()));
                                        Constants.userList.add(userInfo);
                                    }
                                }
                            }

                            @Override
                            public void onErr(Result<Object> result, Exception e) {

                            }
                        });

                        goHome();
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
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        goHome();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            int tag = json.getInt("ret");
                            int num = json.getInt("data");
                            switch (tag) {
                                case 0:
                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    intent.addFlags(num);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 1:
                                    goHome();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void goHome() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
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
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(BaseApplication.getCurProcessName(getApplicationContext()))) {
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
                        jsonToObeject(imUserStr);

                    }
                }


                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("WelcomeActivity", "--onError" + errorCode);
                }
            });
        }
    }
    private void jsonToObeject(String imUserStr) {
        Type type = new TypeToken<Result<UserBean<User>>>() {}.getType();
        GsonUtil.fromJson(imUserStr, type, new GsonUtil.GsonResult<UserBean<User>>() {

            @Override
            public void onTrue(Result<UserBean<User>> result) {
                for (User user: result.getData().getUserlist()){
                    UserInfo userInfo = new UserInfo(String.valueOf(user.getUid()), user.getUname(), Uri.parse(Api.Url + user.getUrl()));
                    if (!Constants.uidList.contains(String.valueOf(user.getUid()))) {
                        Constants.uidList.add(String.valueOf(user.getUid()));
                        Constants.userList.add(userInfo);
                    }
                }

                goHome();
            }

            @Override
            public void onErr(Result<Object> result, Exception e) {

                goHome();
            }
        });
    }
}
