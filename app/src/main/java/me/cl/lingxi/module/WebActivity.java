package me.cl.lingxi.module;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.view.MoeWebView;
import me.cl.lingxi.webview.MoeChromeClient;
import me.cl.lingxi.webview.MoeWebClient;

public class WebActivity extends BaseActivity {

    private static final String TAG = "WebActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.web_view)
    MoeWebView mWebView;
    @BindView(R.id.video_view)
    FrameLayout mVideoView;

    private String mTittle;
    private String mUrl;

    private MoeWebClient mWebClient;
    private MoeChromeClient mChromeClient;

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

        mWebClient = new MoeWebClient();
        mChromeClient = new MoeChromeClient(mVideoView, new MoeChromeClient.onChangedListener() {
            @Override
            public void onShow() {
                setLandscape();
            }

            @Override
            public void onHide() {
                setPortrait();
            }
        });
        mWebView.setWebViewClient(mWebClient);
        mWebView.setWebChromeClient(mChromeClient);
    }

    private void setLandscape() {
        mWebView.setVisibility(View.GONE);
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setPortrait() {
        mWebView.setVisibility(View.VISIBLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, " 现在是横屏");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, " 现在是竖屏");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            } else {
                String url = mWebView.getUrl();
                if (url.contains(mUrl)) {
                    clearWebView();
                    this.finish();
                } else {
                    mWebView.goBack();
                }
            }
        }
        return true;
    }

    /**
     * 判断是否是全屏
     */
    public boolean inCustomView() {
        return mChromeClient.getCustomView() != null;
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        mChromeClient.onHideCustomView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onResume() {
        resumeWebView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        pauseWebView();
        clearWebView();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        resumeWebView();
        super.onDestroy();
    }

    private void pauseWebView() {
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    private void resumeWebView() {
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onResume();
        }
    }

    private void clearWebView() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
        }
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }
}
