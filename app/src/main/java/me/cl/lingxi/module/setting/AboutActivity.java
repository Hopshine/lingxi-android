package me.cl.lingxi.module.setting;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.entity.AppVersion;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.WebActivity;
import me.cl.lingxi.module.update.UpdateReceiver;
import okhttp3.Call;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.version)
    TextView mVersion;

    private UpdateReceiver mUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "关于", true, 0, null);
        int x = (int) (Math.random() * 3) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "据说这APP隐藏着一个不为人知的秘密！");
        }

        String versionName = "V " + Utils.getAppVersionName(this);
        mVersion.setText(versionName);

        registerBroadcast();
    }

    // 注册广播
    private void registerBroadcast() {
        mUpdateReceiver = new UpdateReceiver();
        IntentFilter mIntentFilter = new IntentFilter(UpdateReceiver.UPDATE_ACTION);
        this.registerReceiver(mUpdateReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        // 注销广播
        if (mUpdateReceiver != null)
            unregisterReceiver(mUpdateReceiver);
        super.onDestroy();
    }

    @OnClick({R.id.app_update, R.id.public_license, R.id.learn_more})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.app_update:
                getAppVersion();
                break;
            case R.id.public_license:
                gotoPublicLicense();
                break;
            case R.id.learn_more:
                String url = "file:///android_asset/about.html";
                gotoWeb("前世今生", url);
                break;
        }
    }

    // 获取版本信息
    public void getAppVersion() {
        OkUtil.post()
                .url(Api.latestVersion)
                .execute(new ResultCallback<Result<AppVersion>>() {
                    @Override
                    public void onSuccess(Result<AppVersion> response) {
                        String code = response.getCode();
                        AppVersion data = response.getData();
                        if ("00000".equals(code) && data != null) {
                            Intent intent = new Intent(UpdateReceiver.UPDATE_ACTION);
                            intent.putExtra("app_version", data);
                            sendBroadcast(intent);
                        } else {
                            Utils.toastShow(AboutActivity.this, "版本信息获取失败");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Utils.toastShow(AboutActivity.this, "版本信息获取失败");
                    }

                    @Override
                    public void onFinish() {
                        Utils.toastShow(AboutActivity.this, "版本信息获取失败");
                    }
                });

    }

    private void gotoPublicLicense() {
        Intent intent = new Intent(this, PublicLicenseActivity.class);
        startActivity(intent);
    }

    // 前往web页
    private void gotoWeb(String tittle, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
