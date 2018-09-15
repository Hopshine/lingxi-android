package me.cl.lingxi.module.webview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import me.cl.lingxi.view.webview.MoeChromeClient;
import me.cl.lingxi.view.webview.MoeWebClient;
import me.cl.lingxi.view.webview.MoeWebView;

/**
 * WebActivity
 * 设置独立的web进程，与主进程隔开
 * {@code <activity android:name=".webview.WebViewActivity" android:launchMode="singleTop" android:process=":remote" android:screenOrientation="unspecified" />}
 */
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
        setContentView(R.layout.web_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        }
        if (bundle != null) {
            if ("text/plain".equals(intent.getType())) {
                mTittle = bundle.getString(Intent.EXTRA_TITLE);
                mUrl = bundle.getString(Intent.EXTRA_TEXT);
                mUrl = TextUtils.isEmpty(mUrl) ? "http://47.100.245.128" : mUrl;
                mUrl = mUrl.replaceAll("\t", "");
                mUrl = mUrl.replaceAll("\n", "");
                int i = mUrl.indexOf("http");
                if (TextUtils.isEmpty(mTittle)) {
                    mTittle = mUrl.substring(0, i);
                } else {
                    mTittle = "分享内容";
                }
                mUrl = mUrl.substring(i, mUrl.length());
            } else {
                mTittle = bundle.getString("tittle");
                mUrl = bundle.getString("url");
            }
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

        mWebView.loadUrl(mUrl);
    }

    private void setLandscape() {
        mWebView.setVisibility(View.GONE);
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setPortrait() {
        mWebView.setVisibility(View.VISIBLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 清除常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (inCustomView()) {
                    hideCustomView();
                } else {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        clearWebView();
                        this.finish();
                    }
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
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
    }

    @Override
    protected void onResume() {
        resumeWebView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        pauseWebView();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearWebView();
        super.onDestroy();
        // 因为独立的web进程，与主进程隔开，在关闭WebActivity时销毁进程
        System.exit(0);
    }

    private void pauseWebView() {
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    private void resumeWebView() {
        mWebView.resumeTimers();
        mWebView.onResume();
    }

    private void clearWebView() {
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.loadUrl("about:blank");
        mWebView.pauseTimers();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }
}
