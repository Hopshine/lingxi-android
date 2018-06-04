package me.cl.lingxi.view.webview;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/13
 * desc   : WebClient
 * version: 1.0
 */
public class MoeWebClient extends WebViewClient {

    private static final String TAG = "MoeWebClient";

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        if (url.startsWith("http")) {
            view.loadUrl(url);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.loadUrl("javascript:(function() { " +
                "       var ad = document.getElementById('fage');" +
                "       if(ad != null && ad.style.display == 'block'){" +
                "           ad.style.display = 'none';" +
                "       };" +
                "   })()");
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        url = url.toLowerCase();
        if (isAd(url)) {
            return new WebResourceResponse(null, null, null);
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    // 网页去广告
    private boolean isAd(String url) {
        List<String> filterUrls = new ArrayList<>();
        filterUrls.add("tbv.dbkmwz.cn");
        filterUrls.add("img.cdxzx-tech.com");
        filterUrls.add("97.64.39.220");
        filterUrls.add("s13.cnzz.com");
        filterUrls.add("js.wo-x.cn");
        filterUrls.add("js.erdsyzb.com");
        filterUrls.add("hm.baidu.com");
        for (String adUrl : filterUrls) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}
