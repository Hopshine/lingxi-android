package me.happycao.exoplayer.ui;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/03/31
 * desc   :
 * version: 1.0
 */
public class ExoPlayerManager {

    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory;

    private ExoPlayerManager() {
    }

    public static ExoPlayerManager getInstance() {
        return Holder.holder;
    }

    private static class Holder {
        static ExoPlayerManager holder = new ExoPlayerManager();
    }

    public void setPlayerView(SimpleExoPlayerView playerView) {
        this.playerView = playerView;
        Context context = playerView.getContext();
        // 创建带宽对象
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // 根据当前宽带来创建选择磁道工厂对象
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        // 传入工厂对象，以便创建选择磁道对象
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 根据选择磁道创建播放器对象
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // 将player和view绑定
        this.playerView.setPlayer(player);
        // 定义数据源工厂对象
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()));
        // 创建Extractor工厂对象，用于提取多媒体文件
        extractorsFactory = new DefaultExtractorsFactory();
    }

    public void setUrl(String url) {
        if (player != null) {
            // 添加数据源到播放器中
            player.prepare(getMediaSource(url));
            player.setPlayWhenReady(false);
        }
    }

    public void startPlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void initPlayer(SimpleExoPlayerView playerView, String url) {
        if (player == null) {
            setPlayerView(playerView);
            setUrl(url);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            // 释放播放器对象
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private MediaSource getMediaSource(String url) {
        Uri uri = Uri.parse(url);
        if (url.endsWith(".m3u8")) {
            return new HlsMediaSource(uri, mediaDataSourceFactory, null, null);
        } else {
            return new ExtractorMediaSource(uri, mediaDataSourceFactory, extractorsFactory, null, null);
        }
    }

    public static Builder init(SimpleExoPlayerView playerView) {
        return new Builder(playerView);
    }

    public static class Builder {

        private SimpleExoPlayerView playerView;
        private SimpleExoPlayer player;
        private DefaultTrackSelector trackSelector;
        private DataSource.Factory mediaDataSourceFactory;
        private DefaultExtractorsFactory extractorsFactory;

        public Builder(SimpleExoPlayerView playerView) {
            this.playerView = playerView;
            Context context = playerView.getContext();
            // 创建带宽对象
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            // 根据当前宽带来创建选择磁道工厂对象
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            // 传入工厂对象，以便创建选择磁道对象
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            // 根据选择磁道创建播放器对象
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            // 将player和view绑定
            this.playerView.setPlayer(player);
            // 定义数据源工厂对象
            mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getPackageName()));
            // 创建Extractor工厂对象，用于提取多媒体文件
            extractorsFactory = new DefaultExtractorsFactory();
        }

        public Builder setUrl(String url) {
            // 添加数据源到播放器中
            player.prepare(getMediaSource(url));
            return this;
        }

        public void start() {
            player.setPlayWhenReady(false);
        }

        private MediaSource getMediaSource(String url) {
            Uri uri = Uri.parse(url);
            if (url.endsWith(".m3u8")) {
                return new HlsMediaSource(uri, mediaDataSourceFactory, null, null);
            } else {
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, extractorsFactory, null, null);
            }
        }
    }
}
