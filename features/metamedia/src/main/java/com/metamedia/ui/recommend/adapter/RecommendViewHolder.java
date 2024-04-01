package com.metamedia.ui.recommend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.Clock;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.analytics.AnalyticsCollector;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import com.common.route.IAppRoute;
import com.framework.core.compat.UILog;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.widget.UIImageView;
import com.metamedia.R;
import com.metamedia.bean.Video;
import com.metamedia.widget.PlayerView;
import com.metamedia.widget.SimplePlayerLayout;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-01-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class RecommendViewHolder<T extends Video> extends UIViewHolder<T> {
    @NonNull
    protected final SimplePlayerLayout playerLayout;
    @NonNull
    private final View playerCommentLayout;
    @NonNull
    private final UIImageView playerPersonImageView;

    public RecommendViewHolder(@NonNull LayoutInflater inflater,
                               @NonNull ViewGroup parent) {
        super(inflater.inflate(R.layout.item_video_layout, parent, false));
        this.playerLayout = this.requireViewById(R.id.simplePlayerLayout);
        this.playerCommentLayout = this.requireViewById(R.id.playerCommentLayout);
        this.playerPersonImageView = this.requireViewById(R.id.playerPersonImageView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.previewPlayer();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final Player player = this.getPlayer();
        if (player != null) {
            player.stop();
            player.clearMediaItems();
        }
    }

    @Override
    public void onRecycled() {
        super.onRecycled();
        this.destroy();
    }

    @Override
    public void onInit(@NonNull List<Object> payloads) {
        super.onInit(payloads);
        final T item = this.findData();
        this.playerCommentLayout.setOnClickListener(widget -> {
            IAppRoute
                    .get()
                    .getDrawerController(this)
                    .openCommentComponent();
        });
        this.playerPersonImageView.setOnClickListener(widget -> {
            IAppRoute
                    .get()
                    .getDrawerController(this)
                    .openPersonComponent();
        });
    }

    void play() {
        this.playerLayout.play();
    }

    void pause() {
        this.playerLayout.pause();
    }

    void destroy() {
        this.playerLayout.setPlayer(null);
    }

    boolean isPlaying() {
        return this.playerLayout.isPlaying();
    }

    @Nullable
    public Player getPlayer() {
        return this.playerLayout.getPlayer();
    }

    @SuppressLint("UnsafeOptInUsageError")
    protected void previewPlayer() {
        final T item = this.findData();
        final Uri uri = RawResourceDataSource
                .buildRawResourceUri(item.getRawId());
        final MediaItem mediaItem = MediaItem.fromUri(uri);
        final RecommendAdapter<T> ad = this.requireAdapter();
        final MediaSource mediaSource;
        mediaSource = ad.getMediaSource(mediaItem);
        final SimplePlayerLayout pl = this.playerLayout;
        ExoPlayer player = (ExoPlayer) pl.getPlayer();
        if (player == null) {
            player = this.createPlayer(pl.getContext());
            player.setRepeatMode(Player.REPEAT_MODE_ONE);

            pl.setPlayer(player);
            pl.setResizeMode(PlayerView.RESIZE_MODE_ZOOM);
        }
        player.setMediaSource(mediaSource);
        player.prepare();
    }

    @SuppressLint("UnsafeOptInUsageError")
    @NonNull
    protected ExoPlayer createPlayer(@NonNull Context context) {
        // 创建分析收集器
        final AnalyticsCollector analyticsCollector;
        analyticsCollector = new DefaultAnalyticsCollector(Clock.DEFAULT);
        analyticsCollector.addListener(new AnalyticsListener() {
            @Override
            public void onPlayerError(@NonNull EventTime eventTime,
                                      @NonNull PlaybackException error) {
                UILog.e(error.errorCode + ": " + error.getErrorCodeName());
            }
        });
        // 创建轨道选择工厂
        final TrackSelector trackSelector;
        trackSelector = new DefaultTrackSelector(context);
        // 创建渲染工厂
        final RenderersFactory renderersFactory;
        renderersFactory = new DefaultRenderersFactory(context)
                .setEnableDecoderFallback(true);
        // 创建网络带宽
        final BandwidthMeter bandwidthMeter;
        bandwidthMeter = new DefaultBandwidthMeter.Builder(context)
                .build();
        // build.
        return new ExoPlayer
                .Builder(context)
                .setUseLazyPreparation(true)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(bandwidthMeter)
                .setRenderersFactory(renderersFactory)
                .setAnalyticsCollector(analyticsCollector)
                .build();
    }
}
