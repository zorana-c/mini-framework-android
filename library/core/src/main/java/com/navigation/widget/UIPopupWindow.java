package com.navigation.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.framework.core.R;
import com.framework.core.listener.OnAnimationListener;

/**
 * @Author create by Zhengzelong on 2021/12/24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIPopupWindow extends PopupWindow {
    public static final long INVALID_DURATION = -1L;

    private AnimationOrAnimator mEnterAnimationOrAnimator;
    private AnimationOrAnimator mExitAnimationOrAnimator;
    @AnimRes
    @AnimatorRes
    private int mEnterAnim = -1;
    @AnimRes
    @AnimatorRes
    private int mExitAnim = -1;

    public UIPopupWindow() {
        super();
    }

    public UIPopupWindow(int width, int height) {
        super(width, height);
    }

    public UIPopupWindow(@NonNull Context context) {
        super(context);
    }

    public UIPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UIPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UIPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public UIPopupWindow(@NonNull View contentView) {
        super(contentView);
    }

    public UIPopupWindow(@NonNull View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public UIPopupWindow(@NonNull View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void showAtLocation(@NonNull View parent, int gravity, int x, int y) {
        final long durationLong = this.startEnterAnim();
        super.showAtLocation(parent, gravity, x, y);
        if (INVALID_DURATION != durationLong) {
            this.startBackgroundEnterAnim(durationLong);
        }
    }

    @Override
    public void showAsDropDown(@NonNull View anchor, int offsetX, int offsetY, int gravity) {
        final long durationLong = this.startEnterAnim();
        super.showAsDropDown(anchor, offsetY, offsetY, gravity);
        if (INVALID_DURATION != durationLong) {
            this.startBackgroundEnterAnim(durationLong);
        }
    }

    @Override
    public void dismiss() {
        final long durationLong = this.startExitAnim();
        if (INVALID_DURATION != durationLong) {
            this.startBackgroundExitAnim(durationLong);
        } else if (!this.isExitAnimRunning()) {
            this.supportDismiss();
        }
    }

    protected void startBackgroundEnterAnim(long durationLong) {
        final View backgroundView = (View) this.getContentView().getParent();
        if (backgroundView == null) {
            return;
        }
        final Animation animation = AnimationUtils.loadAnimation(backgroundView.getContext(), R.anim.anim_alpha_action_v);
        animation.setDuration(durationLong);
        animation.setFillAfter(true);
        backgroundView.clearAnimation();
        backgroundView.startAnimation(animation);
    }

    protected void startBackgroundExitAnim(long durationLong) {
        final View backgroundView = (View) this.getContentView().getParent();
        if (backgroundView == null) {
            return;
        }
        final Animation animation = AnimationUtils.loadAnimation(backgroundView.getContext(), R.anim.anim_alpha_action_inv);
        animation.setDuration(durationLong);
        animation.setFillAfter(true);
        backgroundView.clearAnimation();
        backgroundView.startAnimation(animation);
    }

    public final void supportDismiss() {
        super.dismiss();
    }

    @AnimRes
    @AnimatorRes
    public int getEnterAnim() {
        return this.mEnterAnim;
    }

    public void setEnterAnim(@AnimRes @AnimatorRes int enterAnim) {
        this.mEnterAnim = enterAnim;
    }

    @AnimRes
    @AnimatorRes
    public int getExitAnim() {
        return this.mExitAnim;
    }

    public void setExitAnim(@AnimRes @AnimatorRes int exitAnim) {
        this.mExitAnim = exitAnim;
    }

    @Nullable
    public Context getContext() {
        final View contentView = this.getContentView();
        return contentView == null ? null : contentView.getContext();
    }

    @NonNull
    public final Context requireContext() {
        final Context context = this.getContext();
        if (context == null) {
            throw new NullPointerException("NOT CONTENT VIEW SET");
        }
        return context;
    }

    public final boolean isEnterAnimRunning() {
        return this.mEnterAnimationOrAnimator != null
                && this.mEnterAnimationOrAnimator.isRunning();
    }

    public final boolean isExitAnimRunning() {
        return this.mExitAnimationOrAnimator != null
                && this.mExitAnimationOrAnimator.isRunning();
    }

    private long startEnterAnim() {
        if (this.isShowing()) {
            return INVALID_DURATION;
        }
        final View contentView = this.getContentView();
        if (contentView == null) {
            return INVALID_DURATION;
        }
        if (this.mExitAnimationOrAnimator != null) {
            this.mExitAnimationOrAnimator.cancel();
        }
        if (this.mEnterAnimationOrAnimator != null) {
            if (this.mEnterAnimationOrAnimator.isRunning()) {
                return INVALID_DURATION;
            }
            this.mEnterAnimationOrAnimator.cancel();
        }
        final int enterAnim = this.mEnterAnim;
        if (enterAnim != -1) {
            this.mEnterAnimationOrAnimator = this.makeAnimationOrAnimator(enterAnim);
            if (this.mEnterAnimationOrAnimator != null) {
                return this.mEnterAnimationOrAnimator.start(contentView);
            }
        }
        return INVALID_DURATION;
    }

    private long startExitAnim() {
        if (!this.isShowing()) {
            return INVALID_DURATION;
        }
        final View contentView = this.getContentView();
        if (contentView == null) {
            return INVALID_DURATION;
        }
        if (this.mEnterAnimationOrAnimator != null) {
            this.mEnterAnimationOrAnimator.cancel();
        }
        if (this.mExitAnimationOrAnimator != null) {
            if (this.mExitAnimationOrAnimator.isRunning()) {
                return INVALID_DURATION;
            }
            this.mExitAnimationOrAnimator.cancel();
        }
        final int exitAnim = this.mExitAnim;
        if (exitAnim != -1) {
            this.mExitAnimationOrAnimator = this.makeAnimationOrAnimator(exitAnim);
            if (this.mExitAnimationOrAnimator != null) {
                this.mExitAnimationOrAnimator.setListener(this::supportDismiss);
                return this.mExitAnimationOrAnimator.start(contentView);
            }
        }
        return INVALID_DURATION;
    }

    @Nullable
    private AnimationOrAnimator makeAnimationOrAnimator(@AnimatorRes @AnimRes int animId) {
        final Animation animation = this.onCreateAnimation(animId);
        if (animation != null) {
            return new AnimationOrAnimator(animation);
        }
        final Animator animator = this.onCreateAnimator(animId);
        if (animator != null) {
            return new AnimationOrAnimator(animator);
        }
        return null;
    }

    @Nullable
    protected Animation onCreateAnimation(@AnimRes int animation) {
        try {
            return AnimationUtils.loadAnimation(this.requireContext(), animation);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    protected Animator onCreateAnimator(@AnimatorRes int animator) {
        try {
            return AnimatorInflater.loadAnimator(this.requireContext(), animator);
        } catch (Exception e) {
            return null;
        }
    }

    static final class AnimationOrAnimator implements Runnable {
        private Handler handler;
        private Animator animator;
        private Animation animation;
        private OnAnimListener listener;

        AnimationOrAnimator(@NonNull Animator animator) {
            this.animation = null;
            this.animator = animator;
        }

        AnimationOrAnimator(@NonNull Animation animation) {
            this.animation = animation;
            this.animator = null;
        }

        @Override
        public void run() {
            final OnAnimListener listener;
            synchronized (this) {
                listener = this.listener;
                if (this.handler != null) {
                    this.handler.removeCallbacks(this);
                }
                this.handler = null;
                this.listener = null;
            }
            if (listener != null) {
                listener.onAnimCompleted();
            }
        }

        public void setListener(@Nullable OnAnimListener listener) {
            this.listener = listener;
        }

        public long start(@NonNull final View contentView) {
            long durationLong;
            durationLong = this.startAnimator(contentView);
            if (INVALID_DURATION != durationLong) {
                return durationLong;
            }
            return this.startAnimation(contentView);
        }

        private long startAnimator(@NonNull final View contentView) {
            if (this.animator == null) {
                return INVALID_DURATION;
            }
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                    animation.removeListener(this);
                    animCompleted(animation.getDuration());
                }
            });
            final long durationLong = this.animator.getDuration();
            this.animator.setTarget(contentView);
            this.animator.start();
            this.animCompleted(durationLong);
            return durationLong;
        }

        private long startAnimation(@NonNull final View contentView) {
            if (this.animation == null) {
                return INVALID_DURATION;
            }
            this.animation.setAnimationListener(new OnAnimationListener() {
                @Override
                public void onAnimationStart(@NonNull Animation animation) {
                    animation.setAnimationListener(null);
                    animCompleted(animation.getDuration());
                }
            });
            final long durationLong = this.animation.getDuration();
            contentView.clearAnimation();
            contentView.startAnimation(this.animation);
            this.animCompleted(durationLong);
            return durationLong;
        }

        private synchronized void animCompleted(long duration) {
            if (this.handler == null) {
                this.handler = new Handler(Looper.getMainLooper());
            }
            this.handler.removeCallbacks(this);
            this.handler.postDelayed(this, duration);
        }

        public boolean isRunning() {
            if (this.handler != null
                    && this.handler.hasCallbacks(this)) {
                return true;
            }
            if (this.animator != null && this.animator.isStarted()) {
                return this.animator.isRunning();
            }
            if (this.animation != null && this.animation.hasStarted()) {
                return !this.animation.hasEnded();
            }
            return false;
        }

        public synchronized void cancel() {
            this.listener = null;
            if (this.animator != null) {
                this.animator.cancel();
                this.animator = null;
            }
            if (this.animation != null) {
                this.animation.cancel();
                this.animation = null;
            }
        }

        public interface OnAnimListener {

            void onAnimCompleted();
        }
    }
}
