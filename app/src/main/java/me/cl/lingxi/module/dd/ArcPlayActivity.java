package me.cl.lingxi.module.dd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
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
import me.cl.lingxi.entity.dd.PlayConfig;
import me.cl.lingxi.view.webview.MoeChromeClient;
import me.cl.lingxi.view.webview.MoeWebClient;
import me.cl.lingxi.view.webview.MoeWebView;

/**
 * 播放
 */
public class ArcPlayActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
    @BindView(R.id.config_recycler_view)
    RecyclerView mConfigRecyclerView;
    @BindView(R.id.hive_recycler_view)
    RecyclerView mHiveRecyclerView;
    @BindView(R.id.arc_pic)
    ImageView mArcPic;
    @BindView(R.id.arc_name)
    TextView mArcName;
    @BindView(R.id.arc_type)
    TextView mArcType;
    @BindView(R.id.arc_label)
    TextView mArcLabel;
    @BindView(R.id.arc_desc)
    TextView mArcDesc;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;

    private ArcHiveAdapter mArcHiveAdapter;
    private PlayConfigAdapter mPlayConfigAdapter;
    private String mPlayConfig = "";
    private String mVideoUrl = "";

    private MoeChromeClient mChromeClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arc_play_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setBack()
                .build();

        Intent intent = getIntent();
        String arcId = intent.getStringExtra("arc_id");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ArcInfo arcInfo = (ArcInfo) bundle.getSerializable("temp");
            if (arcInfo != null) {
                mArcName.setText(arcInfo.getTypename());
                ContentUtil.loadImage(mArcPic, arcInfo.getSuoluetudizhi());
            }
        }

        initWebView();

        initRecyclerView();
        getData(arcId);
        switchTitle();
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        String payHtml = "file:///android_asset/arcplayer.html";
        MoeWebClient webClient = new MoeWebClient();
        mChromeClient = new MoeChromeClient(mVideoView, new MoeChromeClient.onChangedListener() {
            @Override
            public void onFullscreen(boolean fullscreen) {
                if (fullscreen) {
                    mWebView.setVisibility(View.GONE);
                } else {
                    mWebView.setVisibility(View.VISIBLE);
                }
                setFullscreen(fullscreen);
            }
        });
        mWebView.setWebViewClient(webClient);
        mWebView.setWebChromeClient(mChromeClient);

        mWebView.loadUrl(payHtml);

    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.HORIZONTAL, 10, Color.parseColor("#ffffff"));
        itemDecoration.setGoneLast(true);
        // 剧集
        LinearLayoutManager hiveLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mHiveRecyclerView.setLayoutManager(hiveLayoutManager);
        mHiveRecyclerView.setItemAnimator(new ItemAnimator());
        mHiveRecyclerView.addItemDecoration(itemDecoration);

        List<Hive> hives = new ArrayList<>();
        mArcHiveAdapter = new ArcHiveAdapter(hives);
        mHiveRecyclerView.setAdapter(mArcHiveAdapter);
        mArcHiveAdapter.setOnItemListener(new ArcHiveAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Hive hive, int position) {
                getHiveDetail(hive);
            }
        });

        // 播放源
        LinearLayoutManager configLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mConfigRecyclerView.setLayoutManager(configLayoutManager);
        mConfigRecyclerView.setItemAnimator(new ItemAnimator());
        mConfigRecyclerView.addItemDecoration(itemDecoration);

        List<String> configs = new ArrayList<>();
        mPlayConfigAdapter = new PlayConfigAdapter(configs);
        mConfigRecyclerView.setAdapter(mPlayConfigAdapter);
        mPlayConfigAdapter.setOnItemListener(new PlayConfigAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, String playConfig) {
                mPlayConfig = playConfig;
                loadPlay();
            }
        });
    }

    /**
     * 获取网络数据
     */
    private void getData(String arcId) {
        getPlayConfig();
        getArcInfo(arcId);
        getArcHive(arcId);
    }

    /**
     * 获取播放配置
     */
    private void getPlayConfig() {
        OkUtil.arc()
                .url(DDApi.playConfig)
                .addParam("id", "1")
                .execute(new ArcCallback<PlayConfig>() {
                    @Override
                    public void onSuccess(PlayConfig response) {
                        List<String> line = response.getLine();
                        if (line != null) {
                            if (line.contains(" ")) {
                                line.remove(" ");
                            }
                            mPlayConfig = line.get(0);
                            mPlayConfigAdapter.setData(line);
//                            mConfigRecyclerView.scrollToPosition(0);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }

    /**
     * 获取番剧信息
     */
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

    /**
     * 获取番剧剧集
     */
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
                            mHiveRecyclerView.scrollToPosition(list.size() - 1);
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

    /**
     * 获取番剧详情
     */
    private void getHiveDetail(Hive hive) {
        String id = hive.getId();
        OkUtil.arc()
                .url(DDApi.arcHiveInfo)
                .addParam("id", id)
                .execute(new ArcCallback<HiveInfo>() {
                    @Override
                    public void onSuccess(HiveInfo response) {
                        HiveDetail detail = response.getDetail();
                        mVideoUrl = detail.getBody();
                        loadPlay();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    /**
     * 设置番剧信息
     */
    private void setArcInfo(ArcInfo arcInfo) {
        mTitleName.setText(arcInfo.getTypename());
        mArcName.setText(arcInfo.getTypename());
        mArcType.setText(arcInfo.getZhuangtai());
        mArcLabel.setText(arcInfo.getBiaoqian());
        mArcDesc.setText(arcInfo.getDescription());
        ContentUtil.loadImage(mArcPic, arcInfo.getSuoluetudizhi());

        String xiazaidizhi = arcInfo.getXiazaidizhi();

        List<ArcQuarter> duoJiInfo = arcInfo.getDuoji_info();

    }

    /**
     * 加载播放
     */
    private void loadPlay() {
        if (TextUtils.isEmpty(mVideoUrl)) {
            showToast("获取资源失败，请重试或观看其他番剧");
            return;
        }
        String url = mPlayConfig + mVideoUrl;
        mWebView.loadUrl(url);
    }


    /**
     * 标题切换
     */
    private void switchTitle() {
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

    @OnClick({R.id.arc_pic, R.id.web_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arc_pic:
                onClickWebView();
                break;
            case R.id.web_view:

                break;
        }
    }

    /**
     * 隐藏状态栏
     */
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

    /**
     * 设置WebView链接，模拟器5.0失效，暂不使用
     */
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
