package me.cl.lingxi.view.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.cl.lingxi.BuildConfig;

/**
 * WebView
 * Created by bafsj on 2016-12-28.
 */

public class MoeWebView extends WebView {

    public MoeWebView(Context context) {
        this(context, null);
    }

    public MoeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        // 相关设置
        WebSettings webSettings = getSettings();
        // 排版适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);

        webSettings.setDatabaseEnabled(false);

        webSettings.setDomStorageEnabled(true);
        // 保存表单数据
        webSettings.setSaveFormData(true);
        // 缓存
        webSettings.setAppCacheEnabled(true);
        // 缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setUseWideViewPort方法设置webview推荐使用的窗口
        // setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        webSettings.setLoadWithOverviewMode(false);
        // 隐藏缩放按钮
        webSettings.setBuiltInZoomControls(true);
        // 可任意比例缩放
        webSettings.setUseWideViewPort(true);
        // 不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

}
