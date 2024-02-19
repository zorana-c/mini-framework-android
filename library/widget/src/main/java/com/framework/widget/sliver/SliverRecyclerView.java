package com.framework.widget.sliver;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-02-28
 * @Email : 171905184@qq.com
 * @Description : RecyclerView 兼容
 */
public class SliverRecyclerView extends RecyclerView implements InheritScroll {
    private final int[] mScrollConsumed = new int[2];

    public SliverRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public SliverRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    @CallSuper
    public void onScrolled(int dx, int dy) {
        this.mScrollConsumed[0] = dx;
        this.mScrollConsumed[1] = dy;
    }

    public boolean scrollStep(int dx, int dy, @NonNull int[] consumed) {
        consumed[0] = 0;
        consumed[1] = 0;

        if (dx != 0 || dy != 0) {
            this.mScrollConsumed[0] = 0;
            this.mScrollConsumed[1] = 0;
            this.scrollBy(dx, dy);
            consumed[0] = this.mScrollConsumed[0];
            consumed[1] = this.mScrollConsumed[1];
        }
        return consumed[0] != 0 || consumed[1] != 0;
    }

    // ########## InheritScroll ##########

    private ScrollingAxesHelper mScrollingAxesHelper;
    private int mInheritScrollType;
    private ViewParent mInheritScrollParent;
    private ViewParent mInheritScrollTarget;

    @Override
    public boolean onStartInheritScroll(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        boolean needsInheritScroll = false;

        if (layoutManager.canScrollHorizontally()) {
            needsInheritScroll = (SliverCompat.SCROLL_AXIS_HORIZONTAL & scrollAxes) != 0;
        }
        if (layoutManager.canScrollVertically()) {
            needsInheritScroll |= (SliverCompat.SCROLL_AXIS_VERTICAL & scrollAxes) != 0;
        }
        return needsInheritScroll;
    }

    @Override
    @CallSuper
    public void onInheritScrollAccepted(@NonNull ViewParent parent, @NonNull ViewParent target, int scrollAxes, int scrollType) {
        this.getScrollingAxesHelper().save(scrollType, scrollAxes);
        this.mInheritScrollType = scrollType;
        this.mInheritScrollParent = parent;
        this.mInheritScrollTarget = target;
        this.stopScroll();
    }

    @Override
    public void onInheritPreScroll(@NonNull ViewParent target, int dx, int dy, @NonNull int[] consumed, int scrollType) {
        if (dx < 0 || dy < 0) {
            final int dxConsumed = consumed[0];
            final int dyConsumed = consumed[1];

            if (this.scrollStep(dx, dy, consumed)) {
                consumed[0] += dxConsumed;
                consumed[1] += dyConsumed;
            }
        }
    }

    @Override
    public void onInheritScroll(@NonNull ViewParent target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        final int offsetX = consumed[0];
        final int offsetY = consumed[1];

        if (this.scrollStep(dxUnconsumed, dyUnconsumed, consumed)) {
            consumed[0] += offsetX;
            consumed[1] += offsetY;
        }
    }

    @Override
    public boolean onInheritPreFling(@NonNull ViewParent target, float velocityX, float velocityY) {
        // no-op
        return false;
    }

    @Override
    public boolean onInheritFling(@NonNull ViewParent target, float velocityX, float velocityY, boolean consumed) {
        if (consumed) {
            return false;
        }
        return this.fling((int) velocityX, (int) velocityY);
    }

    @Override
    @CallSuper
    public void onStopInheritScroll(@NonNull ViewParent target, int scrollType) {
        this.getScrollingAxesHelper().clear(scrollType);
        if (SliverCompat.TYPE_NON_TOUCH == scrollType) {
            this.stopScroll();
        }
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
}
