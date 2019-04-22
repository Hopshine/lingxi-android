package me.cl.lingxi.module.video;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.recycle.ItemAnimator;
import me.cl.library.recycle.ItemDecoration;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.IncSourceAdapter;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.entity.inc.Episode;
import me.cl.lingxi.entity.inc.IncVideo;
import me.cl.lingxi.entity.inc.VideoSource;
import me.happycao.exoplayer.ui.ExoPlayerManager;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/21
 * desc   : 视频播放
 * version: 1.0
 */
public class IncVideoPlayActivity extends BaseActivity {

    public static final String INC_VIDEO = "inc_video";

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
    @BindView(R.id.player_view)
    SimpleExoPlayerView mExoPlayerView;
    @BindView(R.id.video_pic)
    ImageView mVideoPic;
    @BindView(R.id.video_name)
    TextView mVideoName;
    @BindView(R.id.video_type)
    TextView mVideoType;
    @BindView(R.id.video_label)
    TextView mVideoLabel;
    @BindView(R.id.video_desc)
    TextView mVideoDesc;
    @BindView(R.id.source_recycler_view)
    RecyclerView mSourceRecyclerView;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;

    private IncSourceAdapter mIncSourceAdapter;
    private ExoPlayerManager mExoPlayerManager;
    private String videoName = "";
    private String videoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setBack()
                .build();

        initRecyclerView();
        initPlayView();
        switchTitle();

        Intent intent = getIntent();
        IncVideo incVideo = (IncVideo) intent.getSerializableExtra(INC_VIDEO);
        if (incVideo != null) {
            setVideoInfo(incVideo);
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.HORIZONTAL, 10, Color.parseColor("#ffffff"));
        itemDecoration.setGoneLast(true);
        // 多资源
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSourceRecyclerView.setLayoutManager(linearLayoutManager);
        mSourceRecyclerView.setItemAnimator(new ItemAnimator());
        mSourceRecyclerView.addItemDecoration(itemDecoration);

        List<VideoSource> sourceList = new ArrayList<>();
        mIncSourceAdapter = new IncSourceAdapter(sourceList);
        mIncSourceAdapter.setOnItemListener(new IncSourceAdapter.OnItemListener() {
            @Override
            public void onItemChildClick(View view, Episode episode) {
                loadPlay(episode);
            }
        });
        mSourceRecyclerView.setAdapter(mIncSourceAdapter);
    }

    private void initPlayView() {
        mExoPlayerManager = new ExoPlayerManager(this);
        mExoPlayerManager.setPlayerView(mExoPlayerView);
        mExoPlayerView.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                switchTitleVisibility(visibility);
            }
        });
    }

    /**
     * 设置视频信息
     */
    private void setVideoInfo(IncVideo incVideo) {
        videoName = incVideo.getName();
        mTitleName.setText(videoName);
        mVideoName.setText(videoName);
        mExoPlayerView.setTitle(videoName);
        mVideoType.setText(incVideo.getType());
        mVideoLabel.setText(incVideo.getLang());
        mVideoDesc.setText(incVideo.getDes());
        ContentUtil.loadImage(mVideoPic, incVideo.getPic());

        // 多资源处理，当前仅支持m3u8
        List<VideoSource> source = incVideo.getSource();
        if (source != null) {
            VideoSource tmp = null;
            for (VideoSource videoSource : source) {
                String title = videoSource.getFlag();
                if (title.endsWith("m3u8")) {
                    tmp = videoSource;
                }
            }
            if (tmp != null) {
                source.clear();
                source.add(tmp);
            }
            mIncSourceAdapter.setDate(source);
        }
    }

    /**
     * 加载播放
     */
    private void loadPlay(Episode episode) {
        videoUrl = episode.getUrl();
        mExoPlayerManager.setUrl(videoUrl);
        mExoPlayerView.setTitle(videoName + episode.getTitle());
    }

    private void setPlay(Episode episode) {
        ExoPlayerManager.init(mExoPlayerView)
                .setUrl(episode.getUrl())
                .start();
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

    /**
     * 显示隐藏状态栏
     */
    private void switchTitleVisibility(int visibility) {
        switch (visibility) {
            case View.GONE:
                mToolbar.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case View.VISIBLE:
                mToolbar.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isFullscreen()) {
                    outFullscreen();
                } else {
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
    public boolean isFullscreen() {
        return mExoPlayerView.isFullscreen();
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void outFullscreen() {
        mExoPlayerView.outFullscreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mExoPlayerManager.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExoPlayerManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExoPlayerManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mExoPlayerManager.onStop();
    }

    @Override
    protected void onDestroy() {
        mExoPlayerManager.onDestroy();
        super.onDestroy();
    }
}
