package com.metamedia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2022/7/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PlayerView extends FrameLayout {
    /**
     * Either the width or height is decreased to obtain the desired aspect ratio.
     */
    public static final int RESIZE_MODE_FIT = 0;
    /**
     * The width is fixed and the height is increased or decreased to obtain the desired aspect ratio.
     */
    public static final int RESIZE_MODE_FIXED_WIDTH = 1;
    /**
     * The height is fixed and the width is increased or decreased to obtain the desired aspect ratio.
     */
    public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
    /**
     * The specified aspect ratio is ignored.
     */
    public static final int RESIZE_MODE_FILL = 3;
    /**
     * Either the width or height is increased to obtain the desired aspect ratio.
     */
    public static final int RESIZE_MODE_ZOOM = 4;

    @IntDef({
            RESIZE_MODE_FIT,
            RESIZE_MODE_FIXED_WIDTH,
            RESIZE_MODE_FIXED_HEIGHT,
            RESIZE_MODE_FILL,
            RESIZE_MODE_ZOOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResizeMode {
    }

    private static final long MIN_UPDATE_INTERVAL_MS = 200L;
    private static final long MAX_UPDATE_INTERVAL_MS = 1000L;
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;

    private final TextureView textureView;
    private final PlayerComponent playerComponent;
    private final PlayerProgressExec playerProgressExec;

    @Nullable
    private Player player;
    @Nullable
    private ArrayList<OnPlayerChangedListener> onPlayerChangedListeners;
    @Nullable
    private ArrayList<OnProgressChangedListener> onProgressChangedListeners;

    // 视频纵横比
    private float videoAspectRatio;
    @ResizeMode
    private int resizeMode = RESIZE_MODE_FIT;

    public PlayerView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("UnsafeOptInUsageError")
    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.playerComponent = new PlayerComponent();
        this.playerProgressExec = new PlayerProgressExec();

        final LayoutParams layoutParams;
        layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        // 播放视频控件
        this.textureView = new TextureView(context);
        this.textureView.setLayoutParams(layoutParams);
        this.addView(this.textureView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio <= 0) {
            // Aspect ratio not set.
            return;
        }
        final int measuredWidth = this.getMeasuredWidth();
        final int measuredHeight = this.getMeasuredHeight();
        int width = measuredWidth;
        int height = measuredHeight;

        final float viewAspectRatio = width * 1.f / height;
        final float aspectDeformation = this.videoAspectRatio / viewAspectRatio - 1.f;
        if (Math.abs(aspectDeformation) <= MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
            return;
        }
        switch (this.resizeMode) {
            case RESIZE_MODE_FIXED_WIDTH:
                height = (int) (width / this.videoAspectRatio);
                break;
            case RESIZE_MODE_FIXED_HEIGHT:
                width = (int) (height * this.videoAspectRatio);
                break;
            case RESIZE_MODE_ZOOM:
                if (aspectDeformation > 0) {
                    width = (int) (height * this.videoAspectRatio);
                } else {
                    height = (int) (width / this.videoAspectRatio);
                }
                break;
            case RESIZE_MODE_FIT:
                if (aspectDeformation > 0) {
                    height = (int) (width / this.videoAspectRatio);
                } else {
                    width = (int) (height * this.videoAspectRatio);
                }
                break;
            case RESIZE_MODE_FILL:
            default:
                // Ignore target aspect ratio
                break;
        }
        if (width != measuredWidth || height != measuredHeight) {
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    public void onPlayerChanged(@Nullable Player oldPlayer,
                                @Nullable Player newPlayer) {
        // no-op
    }

    public void addOnPlayerChangedListener(@NonNull OnPlayerChangedListener listener) {
        if (this.onPlayerChangedListeners == null) {
            this.onPlayerChangedListeners = new ArrayList<>();
        }
        this.onPlayerChangedListeners.add(listener);
        // it
        final Player player = this.player;
        if (player != null) {
            listener.onPlayerChanged(this, null, player);
        }
    }

    public void removeOnPlayerChangedListener(@NonNull OnPlayerChangedListener listener) {
        if (this.onPlayerChangedListeners != null) {
            this.onPlayerChangedListeners.remove(listener);
        }
        // it
        final Player player = this.player;
        if (player != null) {
            listener.onPlayerChanged(this, player, null);
        }
    }

    public void addOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (this.onProgressChangedListeners == null) {
            this.onProgressChangedListeners = new ArrayList<>();
        }
        this.onProgressChangedListeners.add(listener);
    }

    public void removeOnProgressChangedListener(@NonNull OnProgressChangedListener listener) {
        if (this.onProgressChangedListeners != null) {
            this.onProgressChangedListeners.remove(listener);
        }
    }

    @NonNull
    public TextureView getTextureView() {
        return this.textureView;
    }

    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(@Nullable Player player) {
        final Player oldPlayer = this.player;
        if (oldPlayer == player) {
            return;
        }
        if (oldPlayer != null) {
            oldPlayer.stop();
            oldPlayer.release();
            oldPlayer.removeListener(this.playerComponent);
            oldPlayer.clearVideoTextureView(this.textureView);
        }
        this.player = player;
        if (player != null) {
            player.addListener(this.playerComponent);
            player.setVideoTextureView(this.textureView);
        }
        this.dispatchOnPlayerChanged(oldPlayer, player);
    }

    @ResizeMode
    public int getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(@ResizeMode int resizeMode) {
        if (this.resizeMode != resizeMode) {
            this.resizeMode = resizeMode;
            this.requestLayout();
        }
    }

    public float getAspectRatio() {
        return this.videoAspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        if (this.videoAspectRatio != aspectRatio) {
            this.videoAspectRatio = aspectRatio;
            this.requestLayout();
        }
    }

    public void play() {
        if (this.player != null) {
            this.player.play();
        }
    }

    public void pause() {
        if (this.player != null) {
            this.player.pause();
        }
    }

    public void stop() {
        if (this.player != null) {
            this.player.stop();
        }
    }

    public void release() {
        if (this.player != null) {
            this.player.release();
        }
        this.setPlayer(null);
    }

    public void seekTo(long positionMs) {
        if (this.player != null) {
            this.player.seekTo(positionMs);
        }
    }

    public void seekTo(int mediaItemIndex, long positionMs) {
        if (this.player != null) {
            this.player.seekTo(mediaItemIndex, positionMs);
        }
    }

    public boolean isLoading() {
        if (this.player == null) {
            return false;
        }
        return this.player.isLoading();
    }

    public boolean isPlaying() {
        if (this.player == null) {
            return false;
        }
        return this.player.isPlaying();
    }

    public boolean isPlayingAd() {
        if (this.player == null) {
            return false;
        }
        return this.player.isPlayingAd();
    }

    public int getPlaybackState() {
        if (this.player == null) {
            return Player.STATE_IDLE;
        }
        return this.player.getPlaybackState();
    }

    public boolean getPlayWhenReady() {
        if (this.player == null) {
            return false;
        }
        return this.player.getPlayWhenReady();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        if (this.player != null) {
            this.player.setPlayWhenReady(playWhenReady);
        }
    }

    public int getDeviceVolume() {
        if (this.player == null) {
            return 0;
        }
        return this.player.getDeviceVolume();
    }

    public void setDeviceVolume(int volume) {
        this.setDeviceVolume(volume, C.VOLUME_FLAG_SHOW_UI);
    }

    public void setDeviceVolume(int volume, @C.VolumeFlags int flags) {
        if (this.player != null) {
            this.player.setDeviceVolume(volume, flags);
        }
    }

    public boolean isDeviceMuted() {
        if (this.player == null) {
            return false;
        }
        return this.player.isDeviceMuted();
    }

    public void setDeviceMuted(boolean muted) {
        this.setDeviceMuted(muted, C.VOLUME_FLAG_SHOW_UI);
    }

    public void setDeviceMuted(boolean muted, @C.VolumeFlags int flags) {
        if (this.player != null) {
            this.player.setDeviceMuted(muted, flags);
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void updatePlayerProgress() {
        final Player player;
        synchronized (PlayerView.class) {
            player = this.player;
            this.removeCallbacks(this.playerProgressExec);
        }
        long position = 0;
        int playbackState = Player.STATE_IDLE;

        if (player != null) {
            position = player.getContentPosition();
            playbackState = player.getPlaybackState();
            this.dispatchOnPlayProgress(
                    player.getContentDuration(),
                    player.getContentBufferedPosition(), position);
        }

        if (player != null && player.isPlaying()) {
            final long mediaTimeUntilNextFullSecondMs = 1000 - position % 1000;
            long mediaTimeDelayMs = MAX_UPDATE_INTERVAL_MS;
            mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs);

            final float playbackSpeed = player.getPlaybackParameters().speed;
            long delayMs;
            delayMs = playbackSpeed > 0 ? (long) (mediaTimeDelayMs / playbackSpeed) : MAX_UPDATE_INTERVAL_MS;
            delayMs = Util.constrainValue(delayMs, MIN_UPDATE_INTERVAL_MS, MAX_UPDATE_INTERVAL_MS);
            this.postDelayed(this.playerProgressExec, delayMs);
        } else if (playbackState != Player.STATE_IDLE
                && playbackState != Player.STATE_ENDED) {
            this.postDelayed(this.playerProgressExec, MAX_UPDATE_INTERVAL_MS);
        }
    }

    private void applyTextureViewRotation(int textureViewRotation) {
        final Matrix transformMatrix = new Matrix();
        final float textureViewWidth = this.textureView.getWidth();
        final float textureViewHeight = this.textureView.getHeight();
        if (textureViewWidth != 0
                && textureViewHeight != 0
                && textureViewRotation != 0) {
            final float pivotX = textureViewWidth / 2.f;
            final float pivotY = textureViewHeight / 2.f;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            final RectF originalTextureRect = new RectF(0, 0,
                    textureViewWidth,
                    textureViewHeight);
            final RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(), pivotX, pivotY);
        }
        this.textureView.setTransform(transformMatrix);
    }

    private void dispatchOnPlayerChanged(@Nullable Player oldPlayer,
                                         @Nullable Player newPlayer) {
        this.onPlayerChanged(oldPlayer, newPlayer);

        if (this.onPlayerChangedListeners == null) {
            return;
        }
        for (OnPlayerChangedListener listener : this.onPlayerChangedListeners) {
            listener.onPlayerChanged(this, oldPlayer, newPlayer);
        }
    }

    private void dispatchOnPlayProgress(long totalDuration, long bufferedPos, long position) {
        if (this.onProgressChangedListeners == null) {
            return;
        }
        for (OnProgressChangedListener listener : this.onProgressChangedListeners) {
            listener.onPlayProgress(this, totalDuration, bufferedPos, position);
        }
    }

    private final class PlayerProgressExec implements Runnable {
        @Override
        public void run() {
            updatePlayerProgress();
        }
    }

    private final class PlayerComponent implements Player.Listener,
            OnLayoutChangeListener {
        private int unAppliedRotationDegrees;

        @Override
        public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
            final int width = videoSize.width;
            final int height = videoSize.height;
            final int unAppliedRotationDegrees = videoSize.unappliedRotationDegrees;
            float videoAspectRatio = (height == 0 || width == 0)
                    ? 1.f
                    : (width * videoSize.pixelWidthHeightRatio) / height;
            if (unAppliedRotationDegrees == 90 || unAppliedRotationDegrees == 270) {
                videoAspectRatio = 1.f / videoAspectRatio;
            }
            if (this.unAppliedRotationDegrees != 0) {
                textureView.removeOnLayoutChangeListener(this);
            }
            this.unAppliedRotationDegrees = unAppliedRotationDegrees;
            if (unAppliedRotationDegrees != 0) {
                textureView.addOnLayoutChangeListener(this);
            }
            applyTextureViewRotation(unAppliedRotationDegrees);
            setAspectRatio(videoAspectRatio);
        }

        @Override
        public void onLayoutChange(@NonNull View view,
                                   int left, int top, int right, int bottom,
                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
            applyTextureViewRotation(this.unAppliedRotationDegrees);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (Player.STATE_READY == playbackState
                    || Player.STATE_BUFFERING == playbackState) {
                updatePlayerProgress();
            }
        }
    }

    public interface OnPlayerChangedListener {
        void onPlayerChanged(@NonNull PlayerView playerView,
                             @Nullable Player oldPlayer,
                             @Nullable Player newPlayer);
    }

    public interface OnProgressChangedListener {
        void onPlayProgress(@NonNull PlayerView playerView,
                            long totalDuration,
                            long bufferedPos,
                            long position);
    }
}
