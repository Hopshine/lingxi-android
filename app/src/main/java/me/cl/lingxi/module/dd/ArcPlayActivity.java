package me.cl.lingxi.module.dd;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.config.DDApi;
import me.cl.lingxi.common.okhttp.ArcCallback;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.entity.dd.ArcInfo;
import me.cl.lingxi.entity.dd.ArcQuarter;
import me.cl.lingxi.entity.dd.Hive;
import me.cl.lingxi.entity.dd.HiveDetail;
import me.cl.lingxi.entity.dd.HiveInfo;
import me.cl.lingxi.entity.dd.HiveResult;
import me.cl.lingxi.view.webview.MoeChromeClient;
import me.cl.lingxi.view.webview.MoeWebClient;
import me.cl.lingxi.view.webview.MoeWebView;

/**
 * 播放
 */
public class ArcPlayActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.button_bar)
    RelativeLayout mButtonBar;
    @BindView(R.id.title_name)
    TextView mTitleName;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.web_view)
    MoeWebView mWebView;
    @BindView(R.id.video_view)
    FrameLayout mVideoView;
    @BindView(R.id.arc_pic)
    ImageView mArcPic;
    @BindView(R.id.arc_name)
    TextView mArcName;
    @BindView(R.id.arc_desc)
    TextView mArcDesc;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;

    private ArcInfo mArcInfo;
    private ArcHiveAdapter mArcHiveAdapter;
    private String defaultPlayConfig = "https://www.skyfollowsnow.pro/?url=";

    private MoeWebClient mWebClient;
    private MoeChromeClient mChromeClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arc_play_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "", true, 0, null);

        Intent intent = getIntent();
        String arcId = intent.getStringExtra("arc_id");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mArcInfo = (ArcInfo) bundle.getSerializable(Constants.USER_INFO);
            if (mArcInfo != null) {
                mArcName.setText(mArcInfo.getTypename());
                ContentUtil.loadImage(mArcPic, mArcInfo.getSuoluetudizhi());
            }
        }

        initWebView();

        initRecyclerView();
        getData(arcId);
        switchTitle();
    }

    private void initWebView() {
        String payHtml = "file:///android_asset/arcplayer.html";
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

        mWebView.loadUrl(payHtml);

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.HORIZONTAL, 10, Color.parseColor("#ffffff"));
        itemDecoration.setGoneLast(true);
        mRecyclerView.addItemDecoration(itemDecoration);

        List<Hive> hives = new ArrayList<>();
        mArcHiveAdapter = new ArcHiveAdapter(hives);
        mRecyclerView.setAdapter(mArcHiveAdapter);

        mArcHiveAdapter.setOnItemListener(new ArcHiveAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Hive hive, int position) {
                getHiveDetail(hive);
            }
        });
    }

    private void getData(String arcId) {
        getArcInfo(arcId);
        getArcHive(arcId);
    }

    private void setArcInfo(ArcInfo arcInfo) {
        mTitleName.setText(arcInfo.getTypename());
        mArcName.setText(arcInfo.getTypename());
        mArcDesc.setText(arcInfo.getDescription());
        ContentUtil.loadImage(mArcPic, arcInfo.getSuoluetudizhi());

        String xiazaidizhi = arcInfo.getXiazaidizhi();

        List<ArcQuarter> duoJiInfo = arcInfo.getDuoji_info();

    }

    private void getArcInfo(String arcId) {
        OkUtil.arc()
                .url(DDApi.arcTypeInfo)
                .addParam("typeid", arcId)
                .execute(new ArcCallback<ArcInfo>() {
                    @Override
                    public void onSuccess(ArcInfo response) {
                        setArcInfo(response);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void getArcHive(String arcId) {
        OkUtil.arc()
                .url(DDApi.arcHive)
                .addParam("typeid", arcId)
                .execute(new ArcCallback<HiveResult>() {
                    @Override
                    public void onSuccess(HiveResult response) {
                        List<Hive> list = response.getList();
                        if (list != null) {
                            mArcHiveAdapter.setData(list);
                            mRecyclerView.scrollToPosition(list.size() - 1);
                        }
                        Hive last = response.getLastitem();
                        if (last != null) {
                            getHiveDetail(last);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void getHiveDetail(Hive hive) {
        String id = hive.getId();
        OkUtil.arc()
                .url(DDApi.arcHiveInfo)
                .addParam("id", id)
                .execute(new ArcCallback<HiveInfo>() {
                    @Override
                    public void onSuccess(HiveInfo response) {
                        HiveDetail detail = response.getDetail();
                        setHiveDetail(detail);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void setHiveDetail(HiveDetail detail) {
        String body = detail.getBody();
        String url = defaultPlayConfig + body;
        setPlayUrl(url);
    }

    private void switchTitle() {
        // 标题切换
        mButtonBar.setAlpha(0);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int h = appBarLayout.getTotalScrollRange();
                int offset = Math.abs(verticalOffset);
                if (h == offset) return;

                int bbr = offset - 50 < 0 ? 0 : offset;
                mButtonBar.setAlpha(1f * bbr / h);
            }
        });
    }

    @OnClick({R.id.arc_pic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arc_pic:
                onClickWebView();
                break;
        }
    }

    private void onClickWebView() {
        int visibility = mToolbar.getVisibility();
        showToast(String.valueOf(visibility));
        switch (visibility) {
            case View.VISIBLE:
                mToolbar.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case View.GONE:
                mToolbar.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case View.INVISIBLE:
                break;
        }

    }

    private void setPlayUrl(String playUrl) {
        String script = "javascript:setPlayerUrl('" + playUrl + "')";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.d(TAG, "onReceiveValue: " + value);
                }
            });
        } else {
            mWebView.loadUrl(script);
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (inCustomView()) {
                    hideCustomView();
                } else {
                    clearWebView();
                    onBackPressed();
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

}
