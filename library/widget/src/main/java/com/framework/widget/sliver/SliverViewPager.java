package com.framework.widget.sliver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.framework.widget.R;

import java.lang.reflect.Field;

/**
 * @Author create by Zhengzelong on 2023-02-28
 * @Email : 171905184@qq.com
 * @Description : ViewPager 兼容
 */
public class SliverViewPager extends ViewPager implements
        InheritScroll,
        InheritScrollProvider {
    private boolean mIsFirstLayout = true;
    private boolean mIsUseSmoothScroll = true;
    private boolean mIsUserScrollEnabled = true;

    public SliverViewPager(@NonNull Context context) {
        this(context, null);
    }

    public SliverViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliverViewPager);
        final boolean useSmoothScroll;
        useSmoothScroll = typedArray.getBoolean(R.styleable.SliverViewPager_useSmoothScroll, this.mIsUseSmoothScroll);
        final boolean userScrollEnabled;
        userScrollEnabled = typedArray.getBoolean(R.styleable.SliverViewPager_userScrollEnabled, this.mIsUserScrollEnabled);
        typedArray.recycle();

        this.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int scrollState) {
                stopTargetInheritScroll();
            }
        });
        this.setInheritScrollingEnabled(true);
        this.setUseSmoothScroll(useSmoothScroll);
        this.setUserScrollEnabled(userScrollEnabled);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsFirstLayout = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mIsFirstLayout = false;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.mIsUserScrollEnabled) {
            return super.canScrollHorizontally(direction);
        } else return false;
    }

    @Override
    public void setCurrentItem(int item) {
        if (this.mIsFirstLayout) {
            this.setCurrentItem(item, false);
        } else {
            this.setCurrentItem(item, this.mIsUseSmoothScroll);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    public boolean isUserScrollEnabled() {
        return this.mIsUserScrollEnabled;
    }

    public void setUserScrollEnabled(boolean enabled) {
        this.mIsUserScrollEnabled = enabled;
    }

    public boolean isUseSmoothScroll() {
        return this.mIsUseSmoothScroll;
    }

    public void setUseSmoothScroll(boolean useSmoothScroll) {
        this.mIsUseSmoothScroll = useSmoothScroll;
    }

    @Nullable
    public View findCurrentView() {
        try {
            final int N = this.getChildCount();
            for (int index = N - 1; index >= 0; index--) {
                final View child = this.getChildAt(index);
                final LayoutParams layoutParams;
                layoutParams = (LayoutParams) child.getLayoutParams();
                final Field field;
                field = LayoutParams.class.getDeclaredField("position");
                field.setAccessible(true);
                final int position = (int) field.get(layoutParams);
                final int curPosition = this.getCurrentItem();
                if (!layoutParams.isDecor && position == curPosition) {
                    return child;
                }
            }
            return null;
        } catch (@NonNull Exception e) {
            return null;
        }
    }

    // ########## InheritScroll ##########

    private int mInheritScrollType;
    private ViewParent mInheritScrollParent;
    private ViewParent mInheritScrollTarget;
    private ScrollingAxesHelper mScrollingAxesHelper;

    @Override
    public boolean onStartInheritScroll(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        if (this.getChildCount() <= 0) {
            return false;
        }
        boolean needsInheritScroll = false;

        if ((SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0) {
            needsInheritScroll = true;
        }
        if ((SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0) {
            needsInheritScroll = true;
        }
        return needsInheritScroll;
    }

    @Override
    public void onInheritScrollAccepted(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        this.getScrollingAxesHelper().save(scrollType, scrollAxes);
        this.mInheritScrollType = scrollType;
        this.mInheritScrollParent = parent;
        this.mInheritScrollTarget = target;

        final View currentView = this.findCurrentView();
        if (currentView == null) {
            return;
        }
        this.getInheritScrollHelper().startInheritScroll(currentView, scrollAxes, scrollType);
    }

    @Override
    public void onInheritPreScroll(@NonNull ViewParent target, int dx, int dy, @NonNull int[] consumed, int scrollType) {
        this.dispatchInheritPreScroll(dx, dy, consumed, null, scrollType);
    }

    @Override
    public void onInheritScroll(@NonNull ViewParent target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, null, scrollType);
    }

    @Override
    public boolean onInheritPreFling(@NonNull ViewParent target, float velocityX, float velocityY) {
        return this.dispatchInheritPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onInheritFling(@NonNull ViewParent target, float velocityX, float velocityY, boolean consumed) {
        return this.dispatchInheritFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopInheritScroll(@NonNull ViewParent target, int scrollType) {
        this.getScrollingAxesHelper().clear(scrollType);
        this.stopInheritScroll(scrollType);

        if (SliverCompat.TYPE_NON_TOUCH == scrollType
                || SliverCompat.TYPE_TOUCH == this.mInheritScrollType) {
            this.onInheritScrollFinished(scrollType);
            this.mInheritScrollType = SliverCompat.TYPE_TOUCH;
            this.mInheritScrollParent = null;
            this.mInheritScrollTarget = null;
        }
    }

    public void onInheritScrollFinished(int scrollType) {
        // no-op
    }

    @Override
    public int getInheritScrollAxes() {
        return this.getScrollingAxesHelper().getScrollAxes();
    }

    @SliverCompat.ScrollType
    public int getInheritScrollType() {
        return this.mInheritScrollType;
    }

    @Nullable
    public ViewParent getInheritScrollParent() {
        return this.mInheritScrollParent;
    }

    @Nullable
    public ViewParent getInheritScrollTarget() {
        return this.mInheritScrollTarget;
    }

    public void stopTargetInheritScroll() {
        this.stopTargetInheritScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopTargetInheritScroll(int scrollType) {
        final View inheritScrollTarget = (View) this.mInheritScrollTarget;
        if (inheritScrollTarget != null) {
            SliverCompat.stopInheritScroll(inheritScrollTarget, scrollType);
        }
    }

    @NonNull
    public ScrollingAxesHelper getScrollingAxesHelper() {
        if (this.mScrollingAxesHelper == null) {
            this.mScrollingAxesHelper = new ScrollingAxesHelper(this);
        }
        return this.mScrollingAxesHelper;
    }

    // ########## InheritScrollProvider ##########

    private InheritScrollHelper mInheritScrollHelper;

    @Override
    public boolean isInheritScrollingEnabled() {
        return this.getInheritScrollHelper().isInheritScrollingEnabled();
    }

    @Override
    public void setInheritScrollingEnabled(boolean enabled) {
        this.getInheritScrollHelper().setInheritScrollingEnabled(enabled);
    }

    @Override
    public boolean startInheritScroll(int scrollAxes) {
        return this.startInheritScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean startInheritScroll(int scrollAxes, int scrollType) {
        return this.getInheritScrollHelper().startInheritScroll(scrollAxes, scrollType);
    }

    @Override
    public boolean dispatchInheritPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchInheritPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getInheritScrollHelper().dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchInheritScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchInheritScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int scrollType) {
        return this.getInheritScrollHelper().dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
    }

    @Override
    public boolean dispatchInheritPreFling(float velocityX, float velocityY) {
        return this.getInheritScrollHelper().dispatchInheritPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchInheritFling(float velocityX, float velocityY, boolean consumed) {
        return this.getInheritScrollHelper().dispatchInheritFling(velocityX, velocityY, consumed);
    }

    @Override
    public void stopInheritScroll() {
        this.stopInheritScroll(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public void stopInheritScroll(int scrollType) {
        this.getInheritScrollHelper().stopInheritScroll(scrollType);
    }

    @Nullable
    @Override
    public View getInheritScrollingChild() {
        return this.getInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    @Nullable
    @Override
    public View getInheritScrollingChild(int scrollType) {
        return this.getInheritScrollHelper().getInheritScrollingChild(scrollType);
    }

    @Override
    public boolean hasInheritScrollingChild() {
        return this.hasInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasInheritScrollingChild(int scrollType) {
        return this.getInheritScrollHelper().hasInheritScrollingChild(scrollType);
    }

    @NonNull
    public InheritScrollHelper getInheritScrollHelper() {
        if (this.mInheritScrollHelper == null) {
            this.mInheritScrollHelper = new InheritScrollHelper(this);
        }
        return this.mInheritScrollHelper;
    }
}
