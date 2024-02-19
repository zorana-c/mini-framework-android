package com.metamedia.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import com.framework.core.listener.OnAnimatorListener;
import com.metamedia.R;

/**
 * @Author create by Zhengzelong on 2023-04-21
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PlayerProgressBar extends View {
    private final Paint mBufferedPaint;
    private final Paint mPositionPaint;

    private float mBuffered;
    private float mPosition;

    private int mBufferedColor;
    private int mPositionColor;

    private boolean mHideAfterAnimator;
    private ValueAnimator mBufferedAnimator;
    private ValueAnimator mPositionAnimator;

    private PlayerView mPlayerView;
    private PlayerComponent mPlayerComponent;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public PlayerProgressBar(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public PlayerProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public PlayerProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPositionPaint = new Paint();
        this.mBufferedPaint = new Paint();

        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayerProgressBar);
        final int progressBarColor;
        progressBarColor = typedArray.getColor(R.styleable.PlayerProgressBar_progressBarColor, Color.RED);
        final int bufferedBarColor;
        bufferedBarColor = typedArray.getColor(R.styleable.PlayerProgressBar_bufferedBarColor, Color.WHITE);
        typedArray.recycle();

        this.setProgressBarColor(progressBarColor);
        this.setBufferedBarColor(bufferedBarColor);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPosition >= 0.f) {
            // 绘制进度
            this.drawBuffered(canvas);
            this.drawPosition(canvas);
        } else {
            // 绘制动画
            this.drawLeftProgress(canvas);
            this.drawRightProgress(canvas);
        }
    }

    private void drawPosition(@NonNull Canvas canvas) {
        this.mPositionPaint.setColor(this.mPositionColor);
        final float right = this.getMeasuredWidth() * this.mPosition;
        final int restoreToCount = canvas.save();
        canvas.drawRect(0, 0,
                right, this.getMeasuredHeight(), this.mPositionPaint);
        canvas.restoreToCount(restoreToCount);
    }

    private void drawBuffered(@NonNull Canvas canvas) {
        this.mBufferedPaint.setColor(this.mBufferedColor);
        final float right = this.getMeasuredWidth() * this.mBuffered;
        final int restoreToCount = canvas.save();
        canvas.drawRect(0, 0,
                right, this.getMeasuredHeight(), this.mBufferedPaint);
        canvas.restoreToCount(restoreToCount);
    }

    private void drawLeftProgress(@NonNull Canvas canvas) {
        final float scale = this.mBuffered;
        final float width = this.getMeasuredWidth() / 2.f;
        // 0.4 ~ 0.8
        final int alpha = (int) (255 * (0.4 + 0.4 * scale));
        this.mBufferedPaint.setColor(this.mBufferedColor);
        this.mBufferedPaint.setAlpha(alpha);
        final int restoreToCount = canvas.save();
        canvas.drawRect(width - (width * scale),
                0, width,
                this.getMeasuredHeight(), this.mBufferedPaint);
        canvas.restoreToCount(restoreToCount);
    }

    private void drawRightProgress(@NonNull Canvas canvas) {
        final float scale = this.mBuffered;
        final float width = this.getMeasuredWidth() / 2.f;
        // 0.4 ~ 0.8
        final int alpha = (int) (255 * (0.4 + 0.4 * scale));
        this.mBufferedPaint.setColor(this.mBufferedColor);
        this.mBufferedPaint.setAlpha(alpha);
        final int restoreToCount = canvas.save();
        canvas.drawRect(width, 0,
                width + (width * scale),
                this.getMeasuredHeight(), this.mBufferedPaint);
        canvas.restoreToCount(restoreToCount);
    }

    public boolean isHideAfterAnimator() {
        return this.mHideAfterAnimator;
    }

    /**
     * 设置缓存动画结束后, 是否隐藏进度条.
     */
    public void setHideAfterAnimator(boolean enabled) {
        this.mHideAfterAnimator = enabled;
    }

    public void setProgressBarColor(@ColorInt int progressBarColor) {
        if (this.mPositionColor != progressBarColor) {
            this.mPositionColor = progressBarColor;
            this.invalidate();
        }
    }

    public void setBufferedBarColor(@ColorInt int bufferedBarColor) {
        if (this.mBufferedColor != bufferedBarColor) {
            this.mBufferedColor = bufferedBarColor;
            this.invalidate();
        }
    }

    public void setPosition(@FloatRange(from = 0.f, to = 1.f) float position) {
        if (this.mPosition != position) {
            this.mPosition = position;
            this.invalidate();
        }
    }

    public void setBuffered(@FloatRange(from = 0.f, to = 1.f) float buffered) {
        if (this.mBuffered != buffered) {
            this.mBuffered = buffered;
            this.invalidate();
        }
    }

    public void attachToPlayerView(@Nullable PlayerView playerView) {
        final PlayerView oldPlayerView = this.mPlayerView;
        if (oldPlayerView == playerView) {
            return;
        }
        if (oldPlayerView != null) {
            oldPlayerView.removeOnPlayerChangedListener(this.mPlayerComponent);
        }
        this.resetProgressAnimator();
        this.mPlayerView = playerView;
        if (playerView != null) {
            if (this.mPlayerComponent == null) {
                this.mPlayerComponent = new PlayerComponent();
            }
            playerView.addOnPlayerChangedListener(this.mPlayerComponent);
        }
    }

    private void resetProgressAnimator() {
        this.clearProgressAnimator();
        this.mBuffered = 0.f;
        this.mPosition = 0.f;
        this.invalidate();
    }

    private void clearProgressAnimator() {
        this.cancelBufferedAnimator();
        this.cancelPositionAnimator();
    }

    private void beginBufferedAnimator() {
        this.clearProgressAnimator();
        this.mBufferedAnimator = ValueAnimator.ofFloat(0, 1.f);
        this.mBufferedAnimator.addListener(new OnAnimatorListener() {
            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
                repeatBufferedAnimator((ValueAnimator) animator);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                updateBufferedAnimator((ValueAnimator) animation);
            }
        });
        this.mBufferedAnimator.setDuration(500L);
        this.mBufferedAnimator.setRepeatMode(ValueAnimator.RESTART);
        this.mBufferedAnimator.setRepeatCount(ValueAnimator.INFINITE);
        this.mBufferedAnimator.addUpdateListener(this::updateBufferedAnimator);
        this.mBufferedAnimator.start();
    }

    private void cancelBufferedAnimator() {
        if (this.mBufferedAnimator != null) {
            this.mBufferedAnimator.cancel();
            this.mBufferedAnimator = null;
        }
    }

    private void updateBufferedAnimator(@NonNull ValueAnimator animator) {
        this.mBuffered = (float) animator.getAnimatedValue();
        this.mPosition = -1.f;
        this.postInvalidate();
    }

    private void repeatBufferedAnimator(@NonNull ValueAnimator animator) {
        final PlayerView playerView = this.mPlayerView;
        if (playerView == null) {
            return;
        }
        final Player player = playerView.getPlayer();
        if (player == null) {
            return;
        }
        final long duration = player.getContentDuration();
        final long position = player.getContentPosition();
        final long buffered = player.getContentBufferedPosition();
        final float bs = (float) buffered / duration;
        final float ps = (float) position / duration;
        if (player.isPlaying()
                && (bs >= 1.f || ps >= 1.f || bs > ps)) {
            if (this.mHideAfterAnimator) {
                this.setVisibility(View.GONE);
                this.cancelBufferedAnimator();
            } else {
                this.beginPositionAnimator(duration);
            }
        }
    }

    private void beginPositionAnimator(long durationMs /* ms */) {
        this.clearProgressAnimator();
        this.mPositionAnimator = ValueAnimator.ofFloat(0, 1.f);
        this.mPositionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                updatePositionAnimator((ValueAnimator) animator);
            }
        });
        this.mPositionAnimator.setDuration(durationMs);
        this.mPositionAnimator.setRepeatMode(ValueAnimator.RESTART);
        this.mPositionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        this.mPositionAnimator.setInterpolator(new LinearInterpolator());
        this.mPositionAnimator.addUpdateListener(this::updatePositionAnimator);
        this.mPositionAnimator.start();
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            this.mPositionAnimator.cancel();
            this.mPositionAnimator = null;
        }
    }

    private void updatePositionAnimator(@NonNull ValueAnimator animator) {
        final PlayerView playerView = this.mPlayerView;
        if (playerView == null) {
            return;
        }
        final Player player = playerView.getPlayer();
        if (player == null) {
            return;
        }
        final long duration = player.getContentDuration();
        final long position = player.getContentPosition();
        final long buffered = player.getContentBufferedPosition();
        final float bs = (float) buffered / duration;
        final float ps = (float) position / duration;
        this.mBuffered = Math.max(0.f, Math.min(bs, 1.f));
        this.mPosition = Math.max(0.f, Math.min(ps, 1.f));
        this.postInvalidate();
        if (!player.hasNextMediaItem()
                && player.getRepeatMode() == Player.REPEAT_MODE_OFF
                && player.getPlaybackState() == Player.STATE_ENDED) {
            this.cancelPositionAnimator();
        }
    }

    private final class PlayerComponent implements
            Player.Listener,
            PlayerView.OnPlayerChangedListener {
        @Override
        public void onPlayerChanged(@NonNull PlayerView playerView,
                                    @Nullable Player oldPlayer,
                                    @Nullable Player newPlayer) {
            if (oldPlayer != null) {
                oldPlayer.removeListener(this);
            }
            resetProgressAnimator();
            if (newPlayer != null) {
                newPlayer.addListener(this);
            }
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_IDLE
                    || playbackState == Player.STATE_READY
                    || playbackState == Player.STATE_BUFFERING) {
                resetProgressAnimator();
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            final ValueAnimator bufferedAnimator = mBufferedAnimator;
            final ValueAnimator positionAnimator = mPositionAnimator;
            if (positionAnimator != null) {
                if (isPlaying) {
                    positionAnimator.resume();
                } else {
                    positionAnimator.pause();
                }
                return;
            }
            if (bufferedAnimator == null) {
                beginBufferedAnimator();
            } else {
                if (isPlaying) {
                    bufferedAnimator.resume();
                } else {
                    bufferedAnimator.pause();
                }
            }
        }
    }
}
