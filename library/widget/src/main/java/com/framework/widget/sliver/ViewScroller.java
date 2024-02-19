package com.framework.widget.sliver;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.Size;
import androidx.core.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-08-04
 * @Email : 171905184@qq.com
 * @Description : This class encapsulates scrolling
 * with the ability to overshoot the bounds of a scrolling operation.
 */
public final class ViewScroller {
    public static final int MODE_IDLE = 0;
    public static final int MODE_FLING = 1;
    public static final int MODE_SCROLL = 1 << 1;

    @IntDef(flag = true, value = {
            MODE_IDLE,
            MODE_FLING,
            MODE_SCROLL})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface Mode {
    }

    public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
    public static final int MAX_SCROLL_DURATION = 2000; // ms

    @NonNull
    private static final Interpolator sInterpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

    @NonNull
    public static ViewScroller create(@NonNull View view) {
        return ViewScroller.create(view, null);
    }

    @NonNull
    public static ViewScroller create(@NonNull View view,
                                      @Nullable Callback callback) {
        return new ViewScroller(view, callback);
    }

    @NonNull
    private final View mView;
    @NonNull
    private final int[] mScrollLocation = new int[2];
    @NonNull
    private final int[] mScrollConsumed = new int[2];

    @Nullable
    private Callback mCallback;
    @Nullable
    private Interpolator mInterpolator;
    @Nullable
    private OverScroller mOverScroller;
    @Nullable
    private OverScrollerRun mOverScrollerRun;

    @Mode
    private int mMode;
    private boolean mEatRunOnAnimationRequest;
    private boolean mReSchedulePostAnimationCallback;

    /* package */ ViewScroller(@NonNull View view,
                               @Nullable Callback callback) {
        this.mView = view;
        this.mCallback = callback;

        final Context c = view.getContext();
        if (c == null) {
            throw new NullPointerException();
        }
        this.mInterpolator = ViewScroller.sInterpolator;
        this.mOverScroller = new OverScroller(c, this.mInterpolator);
    }

    @Mode
    public int getCurrentMode() {
        return this.mMode;
    }

    @NonNull
    public <T extends View> T getView() {
        return (T) this.mView;
    }

    @Nullable
    public Callback getCallback() {
        return this.mCallback;
    }

    public void setCallback(@Nullable Callback callback) {
        this.mCallback = callback;
    }

    @Nullable
    public OverScroller getScroller() {
        return this.mOverScroller;
    }

    @Nullable
    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public boolean scrollBy(int dx, int dy) {
        final int[] consumed = this.mScrollConsumed;
        consumed[0] = 0;
        consumed[1] = 0;
        return this.scrollBy(dx, dy, consumed);
    }

    public boolean scrollBy(int dx, int dy, @NonNull int[] consumed) {
        return this.dispatchScrollStep(dx, dy, consumed);
    }

    public void abortScroll() {
        this.abortAnimatedOverScroll();
    }

    public void smoothScrollBy(int dx, int dy) {
        this.smoothScrollBy(dx, dy, UNDEFINED_DURATION);
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        this.smoothScrollBy(dx, dy, duration, null);
    }

    public void smoothScrollBy(int startX,
                               int startY,
                               int dx,
                               int dy,
                               int duration) {
        this.smoothScrollBy(startX, startY, dx, dy, duration, null);
    }

    public void smoothScrollBy(int dx,
                               int dy,
                               int duration,
                               @Nullable Interpolator interpolator) {
        this.smoothScrollBy(0, 0, dx, dy, duration, interpolator);
    }

    public void smoothScrollBy(int startX,
                               int startY,
                               int dx,
                               int dy,
                               int duration,
                               @Nullable Interpolator interpolator) {
        if (duration == UNDEFINED_DURATION) {
            duration = this.computeScrollDuration(dx, dy, 0, 0);
        }
        if (interpolator == null) {
            interpolator = this.mInterpolator;
        }
        if (this.mInterpolator != interpolator) {
            this.mInterpolator = interpolator;

            final Context c = this.mView.getContext();
            if (c == null) {
                throw new NullPointerException();
            }
            this.mOverScroller = new OverScroller(c, interpolator);
        }
        this.mMode = MODE_SCROLL;
        this.mScrollLocation[0] = 0;
        this.mScrollLocation[1] = 0;
        this.mOverScroller.startScroll(startX, startY, dx, dy, duration);

        if (Build.VERSION.SDK_INT < 23) {
            this.mOverScroller.computeScrollOffset();
        }
        this.dispatchScrollStart();
        this.postOnAnimation();
    }

    public void fling(int velocityX, int velocityY) {
        this.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX,
                      int minY, int maxY) {
        this.fling(startX, startY, velocityX, velocityY,
                minX, maxX, minY, maxY, 0, 0);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX,
                      int minY, int maxY,
                      int overX, int overY) {
        if (this.mInterpolator != sInterpolator) {
            this.mInterpolator = sInterpolator;

            final Context c = this.mView.getContext();
            if (c == null) {
                throw new NullPointerException();
            }
            this.mOverScroller = new OverScroller(c, this.mInterpolator);
        }
        this.mMode = MODE_FLING;
        this.mScrollLocation[0] = 0;
        this.mScrollLocation[1] = 0;
        this.mOverScroller.fling(startX, startY, velocityX, velocityY,
                minX, maxX, minY, maxY, overX, overY);
        this.dispatchScrollStart();
        this.postOnAnimation();
    }

    private void doOverScroll() {
        this.mEatRunOnAnimationRequest = true;
        this.mReSchedulePostAnimationCallback = false;

        if (this.mOverScroller.computeScrollOffset()) {
            final int currX = this.mOverScroller.getCurrX();
            final int currY = this.mOverScroller.getCurrY();
            int dx = currX - this.mScrollLocation[0];
            int dy = currY - this.mScrollLocation[1];
            this.mScrollLocation[0] = currX;
            this.mScrollLocation[1] = currY;

            final int[] consumed = this.mScrollConsumed;
            consumed[0] = 0;
            consumed[1] = 0;

            if (this.dispatchScrollStep(dx, dy, consumed)) {
                dx -= consumed[0];
                dy -= consumed[1];
            }

            if (!this.doneScrolling(dx, dy)) {
                this.postOnAnimation();
            } // nothing
        }

        this.mEatRunOnAnimationRequest = false;
        if (this.mReSchedulePostAnimationCallback) {
            this.internalPostOnAnimation();
        } else {
            final int mode = this.mMode;
            this.abortAnimatedOverScroll();
            this.dispatchScrollStop(mode);
        }
    }

    private void postOnAnimation() {
        if (this.mEatRunOnAnimationRequest) {
            this.mReSchedulePostAnimationCallback = true;
        } else {
            this.internalPostOnAnimation();
        }
    }

    private void internalPostOnAnimation() {
        if (this.mOverScrollerRun == null) {
            this.mOverScrollerRun = new OverScrollerRun();
        }
        final View view = this.mView;
        view.removeCallbacks(this.mOverScrollerRun);
        ViewCompat.postOnAnimation(view, this.mOverScrollerRun);
    }

    private void abortAnimatedOverScroll() {
        if (this.mOverScrollerRun != null) {
            this.mView.removeCallbacks(this.mOverScrollerRun);
        }
        this.mOverScroller.abortAnimation();
        this.mMode = MODE_IDLE;
    }

    public boolean doneScrolling(int dx, int dy) {
        if (this.mOverScroller.isFinished()) {
            return true;
        }
        final int currX = this.mOverScroller.getCurrX();
        final int currY = this.mOverScroller.getCurrY();
        boolean finishedX = dx != 0;
        boolean finishedY = dy != 0;
        finishedX |= this.mOverScroller.getFinalX() == currX;
        finishedY |= this.mOverScroller.getFinalY() == currY;
        return finishedX && finishedY;
    }

    public int computeScrollDuration(int dx, int dy, int vx, int vy) {
        final int v = (int) Math.sqrt(vx * vx + vy * vy);
        final int d = (int) Math.sqrt(dx * dx + dy * dy);
        final int absDx = Math.abs(dx);
        final int absDy = Math.abs(dy);
        final boolean horizontal = absDx > absDy;

        final View view = this.mView;
        final int w = view.getWidth();
        final int h = view.getHeight();
        final int containerSize = horizontal ? w : h;
        final int halfContainerSize = containerSize / 2;
        final float distanceRatio = Math.min(1.f, 1.f * d / containerSize);
        final float distance = halfContainerSize + halfContainerSize
                * this.distanceInfluenceForSnapDuration(distanceRatio);

        final int duration;
        if (v > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / v));
        } else {
            final float absDelta = (float) (horizontal ? absDx : absDy);
            duration = (int) (((absDelta / containerSize) + 1) * 300);
        }
        return Math.min(duration, MAX_SCROLL_DURATION);
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * (float) Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    private void dispatchScrollStart() {
        if (this.mCallback != null) {
            this.mCallback.scrollStart(this, this.mMode);
        }
    }

    private boolean dispatchScrollStep(int dx,
                                       int dy,
                                       @NonNull int[] consumed) {
        consumed[0] = 0;
        consumed[1] = 0;
        if (this.mCallback != null) {
            this.mCallback.scrollStep(this, dx, dy, consumed);
        }
        return consumed[0] != 0 || consumed[1] != 0;
    }

    private void dispatchScrollStop(int mode) {
        if (this.mCallback != null) {
            this.mCallback.scrollStop(this, mode);
        }
    }

    private final class OverScrollerRun implements Runnable {
        @Override
        public void run() {
            ViewScroller.this.doOverScroll();
        }
    }

    public interface Callback {

        /**
         * {@link  ViewScroller#getScroller()}
         */
        default void scrollStart(@NonNull ViewScroller scroller,
                                 @Mode int mode) {
            // nothing
        }

        /**
         * {@link  ViewScroller#doneScrolling(int, int)}
         */
        default void scrollStep(@NonNull ViewScroller scroller,
                                int dx,
                                int dy,
                                @NonNull @Size(2) int[] consumed) {
            // nothing
        }

        /**
         * {@link  ViewScroller#abortScroll()}
         */
        default void scrollStop(@NonNull ViewScroller scroller,
                                @Mode int mode) {
            // nothing
        }
    }
}
