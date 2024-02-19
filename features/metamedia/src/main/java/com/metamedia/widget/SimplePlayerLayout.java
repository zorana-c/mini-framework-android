package com.metamedia.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;

import com.framework.core.compat.UILog;
import com.framework.core.compat.UIRes;
import com.metamedia.R;

/**
 * @Author create by Zhengzelong on 2023-04-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SimplePlayerLayout extends FrameLayout {
    @NonNull
    private final ImageView playImageView;
    @NonNull
    private final PlayerView playerView;
    @NonNull
    private final PlayerProgressBar playerProgressBar;

    public SimplePlayerLayout(@NonNull Context context) {
        this(context, null);
    }

    public SimplePlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePlayerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams layoutParams;
        layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        // 播放视频控件
        this.playerView = new PlayerView(context);
        this.playerView.setLayoutParams(layoutParams);
        this.playerView.addOnPlayerChangedListener(new PlayerComponent());
        this.addView(this.playerView);

        layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        // 播放视频按钮
        this.playImageView = new AppCompatImageView(context);
        this.playImageView.setLayoutParams(layoutParams);
        this.playImageView.setImageResource(R.mipmap.ic_video_play);
        this.addView(this.playImageView);

        final int colorInt = Color.parseColor("#30FFFFFF");
        layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = UIRes.dip2px(context, 2.f);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 播放进度条
        this.playerProgressBar = new PlayerProgressBar(context, attrs);
        this.playerProgressBar.setBackgroundColor(colorInt);
        this.playerProgressBar.setLayoutParams(layoutParams);
        this.playerProgressBar.attachToPlayerView(this.playerView);
        this.addView(this.playerProgressBar);

        // 设置可点击
        this.setClickable(true);
        // 初始化状态
        this.notifyPlayerStateChanged();
    }

    @NonNull
    public ImageView getPlayImageView() {
        return this.playImageView;
    }

    @NonNull
    public PlayerView getPlayerView() {
        return this.playerView;
    }

    @NonNull
    public PlayerProgressBar getPlayerProgressBar() {
        return this.playerProgressBar;
    }

    public void setPlayImageRes(@DrawableRes int resId) {
        this.playImageView.setImageResource(resId);
    }

    public void setPlayImage(@Nullable Drawable drawable) {
        this.playImageView.setImageDrawable(drawable);
    }

    public boolean isPlaying() {
        return this.playerView.isPlaying();
    }

    public int getPlaybackState() {
        return this.playerView.getPlaybackState();
    }

    public boolean getPlayWhenReady() {
        return this.playerView.getPlayWhenReady();
    }

    @Nullable
    public Player getPlayer() {
        return this.playerView.getPlayer();
    }

    public void setPlayer(@Nullable Player player) {
        this.playerView.setPlayer(player);
    }

    public void setResizeMode(@PlayerView.ResizeMode int resizeMode) {
        this.playerView.setResizeMode(resizeMode);
    }

    public void play() {
        this.playerView.play();
    }

    public void pause() {
        this.playerView.pause();
    }

    public void stop() {
        this.playerView.stop();
    }

    public void release() {
        this.playerView.release();
    }

    @Override
    public boolean performClick() {
        if (super.performClick()) {
            return true;
        }
        this.togglePlayWhenReady();
        return true;
    }

    public void togglePlayWhenReady() {
        if (this.isPlaying()) this.pause();
        else this.play();
    }

    protected void notifyPlayerStateChanged() {
        boolean showing = false;
        /*
         * 条件:
         * OR 没有播放源
         * OR 播放已完成
         * OR 预览/播放异常
         * OR 进度大于零 && 非播放中
         * */
        final Player player = this.getPlayer();
        if (player == null
                || player.getPlayerError() != null
                || player.getPlaybackState() == Player.STATE_ENDED
                || (!player.isPlaying()
                && player.getCurrentPosition() > 0
                && player.getContentPosition() > 0)) {
            showing = true;
        }
        final int oldVis = this.playImageView.getVisibility();
        final int newVis = showing ? View.VISIBLE : View.GONE;
        if (oldVis == newVis) {
            return;
        }
        this.playImageView.setVisibility(newVis);
        final Animation animation;
        animation = AnimationUtils.loadAnimation(this.getContext(),
                newVis == View.VISIBLE
                        ? R.anim.anim_alpha_action_v
                        : R.anim.anim_alpha_action_inv);
        animation.setDuration(200);
        this.playImageView.clearAnimation();
        this.playImageView.startAnimation(animation);
        if (newVis == View.VISIBLE) {
            this.bringChildToFront(this.playImageView);
        }
    }

    private final class PlayerComponent implements Player.Listener,
            PlayerView.OnPlayerChangedListener {
        @Override
        public void onPlayerChanged(@NonNull PlayerView playerView,
                                    @Nullable Player oldPlayer,
                                    @Nullable Player newPlayer) {
            if (oldPlayer != null) {
                oldPlayer.removeListener(this);
            }
            if (newPlayer != null) {
                newPlayer.addListener(this);
            }
            notifyPlayerStateChanged();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            notifyPlayerStateChanged();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady,
                                           int playWhenReadyReason) {
            notifyPlayerStateChanged();
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            UILog.e(error);
        }
    }
}
