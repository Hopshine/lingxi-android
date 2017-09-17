package me.cl.lingxi.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.WebActivity;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.version)
    TextView mVersion;

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

    }

    @OnClick({R.id.public_license, R.id.learn_more})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.public_license:
                gotoPublicLicense();
                break;
            case R.id.learn_more:
                String url = "file:///android_asset/about.html";
                gotoWeb("前世今生", url);
                break;
        }
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
