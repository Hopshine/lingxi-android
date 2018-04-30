package me.cl.lingxi.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.User;
import me.cl.lingxi.module.main.MainActivity;
import me.cl.lingxi.module.member.LoginActivity;
import okhttp3.Call;

public class WelcomeActivity extends BaseActivity {

    private boolean isLogin;
    private String rcToken;

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
        rcToken = SPUtil.build().getString(Constants.RC_TOKEN);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    if (TextUtils.isEmpty(rcToken)) {
                        Utils.toastShow(WelcomeActivity.this, "登录过期，请重新登录");
                        goLogin();
                    } else {
                        connectRc(rcToken);
                    }
                } else {
                    goLogin();
                }
            }
        }, 1500);
    }

    public void getImUser() {
        String lxToken = Utils.getMetaValue(this, "LINGCI_APP_KEY");
        OkUtil.post()
                .url(Api.listRcUser)
                .addParam("lxToken", lxToken)
                .execute(new ResultCallback<Result<List<User>>>() {
                    @Override
                    public void onSuccess(Result<List<User>> response) {
                        String code = response.getCode();
                        List<User> data = response.getData();
                        if ("00000".equals(code) && data != null) {
                            SPUtil.build().putString(Constants.RC_USER, GsonUtil.toJson(data));
                            serRcUser(data);
                        }
                        goHome(0);
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

    /**
     * 获取未读条数
     */
    public void getUnRead(String userId) {
        OkUtil.post()
                .url(Api.baseUrl)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        goHome(response.getData() == null ? 0 : response.getData());
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
    private void connectRc(final String token) {
        if (getApplicationInfo().packageName.equals(LxApplication.getCurProcessName(getApplicationContext()))) {
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
                    goHome(0);
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("WelcomeActivity", "--onSuccess" + userid);
                    String imUserStr = SPUtil.build().getString(Constants.RC_USER);
                    if (imUserStr == null || imUserStr.length() == 0) {
                        getImUser();
                    } else {
                        List<User> userList = GsonUtil.toList(imUserStr, User[].class);
                        serRcUser(userList);
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

    private void serRcUser(List<User> list) {
        for (User user : list) {
            String id = user.getId();
            UserInfo userInfo = new UserInfo(id, user.getUsername(), Uri.parse(Constants.IMG_URL + user.getAvatar()));
            if (!Constants.uidList.contains(id)) {
                Constants.uidList.add(id);
                Constants.userList.add(userInfo);
            }
        }
    }
}
