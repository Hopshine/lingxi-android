package com.lingci.module;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lingci.R;
import com.lingci.common.view.MoeWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.web_view)
    MoeWebView mWebView;

    private String mTittle;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTittle = bundle.getString("tittle");
            mUrl = bundle.getString("url");
        }

        setupToolbar(mToolbar, mTittle, true, 0, null);
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onResume() {
        resumeVideoView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        pauseVideoView();
        clearVideoView();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        resumeVideoView();
        super.onDestroy();
    }

    private void pauseVideoView() {
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    private void resumeVideoView() {
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onResume();
        }
    }

    private void clearVideoView() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
        }
    }
}
