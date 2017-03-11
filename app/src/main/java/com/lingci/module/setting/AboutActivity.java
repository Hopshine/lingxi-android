package com.lingci.module.setting;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.common.util.MoeToast;
import com.lingci.module.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.about_info)
    WebView mAboutInfo;

    private LinearLayout lc_ruturn;
    private TextView tv_top;
    private WebView about_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "关于普通的APP", true, 0, null);
        int x = (int) (Math.random() * 3) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "据说这APP隐藏着一个不为人知的秘密！");
        }
        mAboutInfo.loadUrl(" file:///android_asset/about.html");
    }

}
