package com.framework.widget.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.framework.widget.R;
import com.framework.widget.sliver.SliverCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2024-01-31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class NestedFrameLayout extends FrameLayout
        implements NestedScrollingChild3 {
    public static final int VERTICAL = LinearLayout.VERTICAL;
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;

    @IntDef({
            VERTICAL,
            HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
        // nothing
    }

    private final int[] mLastTouch = new int[2];
    private final int[] mInitialTouch = new int[2];
    private final int[] mNestedOffsets = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private final int[] mOffsetInWindow = new int[2];

    private final int mScaledTouchSlop;
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;

    @Orientation
    private int mOrientation = HORIZONTAL;
    private int mScrollPointerId;
    @Nullable
    private VelocityTracker mVelocityTracker;

    private boolean mIsBeingDragged = false;
    private boolean mIsUnableToDrag = false;
    private boolean mIsUserScrollEnabled = true;

    public NestedFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public NestedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NestedFrameLayout);
        final int orientation;
        orientation = typedArray.getInt(R.styleable.NestedFrameLayout_android_orientation, HORIZONTAL);
        final boolean userScrollEnabled;
        userScrollEnabled = typedArray.getBoolean(R.styleable.NestedFrameLayout_userScrollEnabled, true);
        typedArray.recycle();
        this.setOrientation(orientation);
        this.setUserScrollEnabled(userScrollEnabled);
        this.setFocusableInTouchMode(true);
        this.setNestedScrollingEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mScaledTouchSlop = configuration.getScaledTouchSlop();
        this.mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            if (this.performInterceptTouchEvent(event)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    protected boolean performInterceptTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_CANCEL
                || actionMasked == MotionEvent.ACTION_UP) {
            this.cancelTouchScroll();
            return false;
        }
        if (actionMasked != MotionEvent.ACTION_DOWN) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                this.mScrollPointerId = event.getPointerId(0);
                this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX() + 0.5f);
                this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY() + 0.5f);
                this.mNestedOffsets[0] = 0;
                this.mNestedOffsets[1] = 0;
                this.mIsUnableToDrag = false;

                int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
                if (this.canScrollHorizontally()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
                }
                if (this.canScrollVertically()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
                }
                this.startNestedScroll(scrollAxes);
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex;
                pointerIndex = event.findPointerIndex(this.mScrollPointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                if (!this.mIsBeingDragged) {
                    final int x = (int) (event.getX(pointerIndex) + 0.5f);
                    final int y = (int) (event.getY(pointerIndex) + 0.5f);
                    final int dx = this.mInitialTouch[0] - x;
                    final int dy = this.mInitialTouch[1] - y;
                    final int dxAbs = Math.abs(dx);
                    final int dyAbs = Math.abs(dy);
                    boolean dragStarted = false;

                    if (this.canScroll(this, x, y,
                            this.canScrollHorizontally() ? dx : 0,
                            this.canScrollVertically() ? dy : 0, false)) {
                        this.mLastTouch[0] = x;
                        this.mLastTouch[1] = y;
                        this.mIsUnableToDrag = true;
                        return false;
                    }

                    if (this.hasNestedScrollingParent()) {
                        if (this.canScrollHorizontally()
                                && dxAbs > this.mScaledTouchSlop && dxAbs * 0.5f > dyAbs) {
                            dragStarted = true;
                        }
                        if (this.canScrollVertically()
                                && dyAbs > this.mScaledTouchSlop && dyAbs * 0.5f > dxAbs) {
                            dragStarted = true;
                        }
                    }
                    if (dragStarted) {
                        this.mLastTouch[0] = x;
                        this.mLastTouch[1] = y;
                        this.mIsBeingDragged = true;
                    } else {
                        if (this.canScrollHorizontally()) {
                            if (dyAbs > this.mScaledTouchSlop) {
                                this.mIsUnableToDrag = true;
                            }
                        } else {
                            if (dxAbs > this.mScaledTouchSlop) {
                                this.mIsUnableToDrag = true;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                this.onSecondaryPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                this.onSecondaryPointerUp(event);
                break;
        }
        return this.mIsBeingDragged;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            if (this.performTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    protected boolean performTouchEvent(@NonNull MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            this.mNestedOffsets[0] = 0;
            this.mNestedOffsets[1] = 0;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        final MotionEvent motionEvent = MotionEvent.obtain(event);
        motionEvent.offsetLocation(
                this.mNestedOffsets[0],
                this.mNestedOffsets[1]);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                this.mScrollPointerId = event.getPointerId(0);
                this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX() + 0.5f);
                this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY() + 0.5f);

                if (this.mIsBeingDragged) {
                    this.requestParentDisallowInterceptTouchEvent();
                }

                int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
                if (this.canScrollHorizontally()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
                }
                if (this.canScrollVertically()) {
                    scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
                }
                this.startNestedScroll(scrollAxes);
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex;
                pointerIndex = event.findPointerIndex(this.mScrollPointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final int x = (int) (event.getX(pointerIndex) + 0.5f);
                final int y = (int) (event.getY(pointerIndex) + 0.5f);
                int dx = this.mLastTouch[0] - x;
                int dy = this.mLastTouch[1] - y;

                if (!this.mIsBeingDragged) {
                    boolean dragStarted = false;
                    int dxAbs = 0;
                    int dyAbs = 0;

                    if (this.hasNestedScrollingParent()) {
                        if (this.canScrollHorizontally()) {
                            dxAbs = Math.abs(dx);
                        }
                        if (this.canScrollVertically()) {
                            dyAbs = Math.abs(dy);
                        }
                    }
                    if (dxAbs > this.mScaledTouchSlop) {
                        if (dx > 0) {
                            dx = Math.max(0, dx - this.mScaledTouchSlop);
                        } else {
                            dx = Math.min(dx + this.mScaledTouchSlop, 0);
                        }
                        if (dx != 0) {
                            dragStarted = true;
                        }
                    }
                    if (dyAbs > this.mScaledTouchSlop) {
                        if (dy > 0) {
                            dy = Math.max(0, dy - this.mScaledTouchSlop);
                        } else {
                            dy = Math.min(dy + this.mScaledTouchSlop, 0);
                        }
                        if (dy != 0) {
                            dragStarted = true;
                        }
                    }
                    if (dragStarted) {
                        this.mIsBeingDragged = true;
                    }
                }
                if (this.mIsBeingDragged) {
                    this.mLastTouch[0] = x;
                    this.mLastTouch[1] = y;
                    dx = this.canScrollHorizontally() ? dx : 0;
                    dy = this.canScrollVertically() ? dy : 0;

                    final int[] scrollConsumed = this.mScrollConsumed;
                    if (this.dispatchScrollBy(dx, dy, scrollConsumed)) {
                        dx -= scrollConsumed[0];
                        dy -= scrollConsumed[1];
                        this.requestParentDisallowInterceptTouchEvent();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                this.onSecondaryPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                this.onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.cancelTouchScroll();
                break;
            case MotionEvent.ACTION_UP:
                this.mVelocityTracker.addMovement(motionEvent);
                this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
                int velocityX = 0;
                int velocityY = 0;
                if (this.canScrollHorizontally()) {
                    velocityX = (int) -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
                }
                if (this.canScrollVertically()) {
                    velocityY = (int) -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
                }
                if (this.dispatchFlingBy(velocityX, velocityY)) {
                    this.resetTouchScroll();
                } else {
                    this.cancelTouchScroll();
                }
                break;
        }
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.addMovement(motionEvent);
        }
        motionEvent.recycle();
        return true;
    }

    private boolean canScroll(@NonNull View target, int x, int y, int dx, int dy, boolean check) {
        if (target instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) target;
            final int scrollX = target.getScrollX();
            final int scrollY = target.getScrollY();
            final int N = viewGroup.getChildCount();
            for (int index = N - 1; index >= 0; index--) {
                final View child = viewGroup.getChildAt(index);
                if (x + scrollX >= child.getLeft()
                        && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop()
                        && y + scrollY < child.getBottom()
                        && this.canScroll(child,
                        x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop(), dx, dy, true)) {
                    return true;
                }
            }
        }
        return check && ((dx != 0 && target.canScrollHorizontally(dx))
                || (dy != 0 && target.canScrollVertically(dy)));
    }

    private void requestParentDisallowInterceptTouchEvent() {
        final ViewParent parent = this.getParent();
        if (parent == null) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(true);
    }

    private void onSecondaryPointerDown(@NonNull MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        this.mScrollPointerId = event.getPointerId(actionIndex);
        this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX(actionIndex) + 0.5f);
        this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY(actionIndex) + 0.5f);
    }

    private void onSecondaryPointerUp(@NonNull MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        if (event.getPointerId(actionIndex) == this.mScrollPointerId) {
            final int newIndex = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = event.getPointerId(newIndex);
            this.mInitialTouch[0] = this.mLastTouch[0] = (int) (event.getX(newIndex) + 0.5f);
            this.mInitialTouch[1] = this.mLastTouch[1] = (int) (event.getY(newIndex) + 0.5f);
        }
    }

    private void resetTouchScroll() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.stopNestedScroll();
    }

    private void cancelTouchScroll() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        this.resetTouchScroll();
    }

    private boolean dispatchFlingBy(int velocityX, int velocityY) {
        if (Math.abs(velocityX) < this.mMinFlingVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < this.mMinFlingVelocity) {
            velocityY = 0;
        }
        if (velocityX == 0 && velocityY == 0) {
            return false;
        }
        if (this.dispatchNestedPreFling(velocityX, velocityY)) {
            return false;
        }
        this.dispatchNestedFling(velocityX, velocityY, false);
        return false;
    }

    private boolean dispatchScrollBy(int dx, int dy, @NonNull int[] scrollConsumed) {
        final int[] offsetInWindow = this.mOffsetInWindow;
        int dxConsumed = 0;
        int dyConsumed = 0;

        if (this.dispatchNestedPreScroll(dx, dy, scrollConsumed, offsetInWindow)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        if (this.dispatchNestedScroll(dxConsumed, dyConsumed, dx, dy, offsetInWindow, scrollConsumed)) {
            dx -= scrollConsumed[0];
            dy -= scrollConsumed[1];
            dxConsumed += scrollConsumed[0];
            dyConsumed += scrollConsumed[1];
            this.mLastTouch[0] -= offsetInWindow[0];
            this.mLastTouch[1] -= offsetInWindow[1];
            this.mNestedOffsets[0] += offsetInWindow[0];
            this.mNestedOffsets[1] += offsetInWindow[1];
        }

        scrollConsumed[0] = dxConsumed;
        scrollConsumed[1] = dyConsumed;
        return scrollConsumed[0] != 0 || scrollConsumed[1] != 0;
    }

    public boolean isUserScrollEnabled() {
        return this.mIsUserScrollEnabled;
    }

    public void setUserScrollEnabled(boolean enabled) {
        this.mIsUserScrollEnabled = enabled;
    }

    @Orientation
    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(@Orientation int orientation) {
        this.mOrientation = orientation;
    }

    public final boolean canScrollHorizontally() {
        return this.mOrientation == HORIZONTAL;
    }

    public final boolean canScrollVertically() {
        return this.mOrientation == VERTICAL;
    }

    // NestedScrollingChild
    private final int[] mNestedScrollConsumed = new int[2];

    @Nullable
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    @Override
    public boolean isNestedScrollingEnabled() {
        return this.getNestedScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        this.getNestedScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return this.hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return this.getNestedScrollingChildHelper().hasNestedScrollingParent(type);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return this.startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return this.getNestedScrollingChildHelper().startNestedScroll(axes, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return this.getNestedScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        final int[] nestedScrollConsumed = this.mNestedScrollConsumed;
        nestedScrollConsumed[0] = 0;
        nestedScrollConsumed[1] = 0;
        this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, nestedScrollConsumed);
        return nestedScrollConsumed[0] != 0 || nestedScrollConsumed[1] != 0;
    }

    // NestedScrollingChild3

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, consumed, type);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, @NonNull int[] consumed) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, consumed, ViewCompat.TYPE_TOUCH);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, @NonNull int[] consumed, int type) {
        this.getNestedScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
        return consumed[0] != 0 || consumed[1] != 0;
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.getNestedScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.getNestedScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopNestedScroll() {
        this.stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll(int type) {
        this.getNestedScrollingChildHelper().stopNestedScroll(type);
    }

    @NonNull
    public NestedScrollingChildHelper getNestedScrollingChildHelper() {
        if (this.mNestedScrollingChildHelper == null) {
            this.mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return this.mNestedScrollingChildHelper;
    }
}
