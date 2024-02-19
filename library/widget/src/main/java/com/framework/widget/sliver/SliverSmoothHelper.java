package com.framework.widget.sliver;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2024-02-06
 * @Email : 171905184@qq.com
 * @Description : 简单滑动辅助工具类
 */
public class SliverSmoothHelper {
    static final int UNDEFINED_DURATION = SliverContainer.UNDEFINED_DURATION;

    @Nullable
    private SliverContainer mSliverContainer;
    @Nullable
    private ComponentListener mComponentListener;

    public void attachToSliverContainer(@Nullable SliverContainer sliverContainer) {
        final SliverContainer oldSliverContainer = this.mSliverContainer;
        if (oldSliverContainer == sliverContainer) {
            return;
        }
        if (oldSliverContainer != null) {
            if (this.mComponentListener != null) {
                oldSliverContainer.removeOnScrollListener(this.mComponentListener);
            }
            this.mComponentListener = null;
        }
        this.mSliverContainer = sliverContainer;
        if (sliverContainer != null) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            sliverContainer.addOnScrollListener(this.mComponentListener);
        }
        this.onSliverContainerChanged(oldSliverContainer, sliverContainer);
    }

    protected void onSliverContainerChanged(@Nullable SliverContainer oldSliverContainer,
                                            @Nullable SliverContainer newSliverContainer) {
    }

    protected void onScrollStateChanged(@NonNull SliverContainer sliverContainer,
                                        @SliverContainer.ScrollState int scrollState) {
    }

    protected void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
    }

    public boolean smoothScrollToHead(@IdRes int id) {
        return this.smoothScrollToHead(id, UNDEFINED_DURATION);
    }

    public boolean smoothScrollToHead(@IdRes int id, int duration) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return false;
        }
        final View target = sliverContainer.findViewById(id);
        return this.smoothScrollToHead(target, duration);
    }

    /**
     * 目标视图滑动到头部
     *
     * @param target 目标视图
     */
    public boolean smoothScrollToHead(@Nullable View target) {
        return this.smoothScrollToHead(target, UNDEFINED_DURATION);
    }

    /**
     * 目标视图滑动到头部
     *
     * @param target   目标视图
     * @param duration 滑动持续时长(毫秒)
     */
    public boolean smoothScrollToHead(@Nullable View target, int duration) {
        if (target == null) {
            return false;
        }
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return false;
        }
        int finalX = this.getFinalX(target);
        int finalY = this.getFinalY(target);
        finalX = Math.min(finalX, this.getMaxFinalX());
        finalY = Math.min(finalY, this.getMaxFinalY());
        return sliverContainer.smoothScrollBy(finalX, finalY, duration);
    }

    public boolean smoothScrollToTail(@IdRes int id) {
        return this.smoothScrollToTail(id, UNDEFINED_DURATION);
    }

    public boolean smoothScrollToTail(@IdRes int id, int duration) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return false;
        }
        final View target = sliverContainer.findViewById(id);
        return this.smoothScrollToTail(target, duration);
    }

    /**
     * 目标视图滑动到底部
     *
     * @param target 目标视图
     */
    public boolean smoothScrollToTail(@Nullable View target) {
        return this.smoothScrollToTail(target, UNDEFINED_DURATION);
    }

    /**
     * 目标视图滑动到底部
     *
     * @param target   目标视图
     * @param duration 滑动持续时长(毫秒)
     */
    public boolean smoothScrollToTail(@Nullable View target, int duration) {
        if (target == null) {
            return false;
        }
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return false;
        }
        final int extentX = sliverContainer.computeHorizontalScrollExtent();
        final int extentY = sliverContainer.computeVerticalScrollExtent();
        int finalX;
        int finalY;
        finalX = this.getFinalX(target) - extentX + target.getMeasuredWidth();
        finalY = this.getFinalY(target) - extentY + target.getMeasuredHeight();
        finalX = Math.min(finalX, this.getMaxFinalX());
        finalY = Math.min(finalY, this.getMaxFinalY());
        return sliverContainer.smoothScrollBy(finalX, finalY, duration);
    }

    public void backScroll(@Nullable RecyclerView recyclerView) {
        this.backScroll(recyclerView, UNDEFINED_DURATION);
    }

    public void backScroll(@Nullable RecyclerView recyclerView, int duration) {
        if (recyclerView == null) {
            return;
        }
        final RecyclerView.LayoutManager layoutManager;
        layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        final int scrollX = layoutManager.canScrollHorizontally()
                ? -this.getScrollX(recyclerView) : 0;
        final int scrollY = layoutManager.canScrollVertically()
                ? -this.getScrollY(recyclerView) : 0;
        if (scrollX == 0 && scrollY == 0) {
            return;
        }
        this.stopNestedScroll(recyclerView, SliverCompat.TYPE_NON_TOUCH);
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        if (layoutManager.canScrollHorizontally()) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
        }
        if (layoutManager.canScrollVertically()) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
        }
        recyclerView.startNestedScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
        recyclerView.smoothScrollBy(scrollX, scrollY, null, duration);
    }

    public void backScroll(@Nullable SliverContainer sliverContainer) {
        this.backScroll(sliverContainer, UNDEFINED_DURATION);
    }

    public void backScroll(@Nullable SliverContainer sliverContainer, int duration) {
        if (sliverContainer == null) {
            return;
        }
        final int scrollX = sliverContainer.canScrollHorizontally()
                ? -this.getScrollX(sliverContainer) : 0;
        final int scrollY = sliverContainer.canScrollVertically()
                ? -this.getScrollY(sliverContainer) : 0;
        if (scrollX == 0 && scrollY == 0) {
            return;
        }
        this.stopNestedScroll(sliverContainer, SliverCompat.TYPE_NON_TOUCH);
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        if (sliverContainer.canScrollHorizontally()) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
        }
        if (sliverContainer.canScrollVertically()) {
            scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
        }
        sliverContainer.startNestedScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
        sliverContainer.smoothScrollBy(scrollX, scrollY, duration);
    }

    public void stopNestedScroll(@NonNull View target,
                                 @SliverCompat.ScrollType int scrollType) {
        if (SliverCompat.hasNestedScrollingParent(target, scrollType)) {
            SliverCompat.stopNestedScroll(target, scrollType);
        }
        if (target == this.mSliverContainer) {
            return;
        }
        final ViewParent parent = target.getParent();
        if (parent instanceof View) {
            this.stopNestedScroll((View) parent, scrollType);
        }
    }

    /**
     * 水平剩余偏移量
     *
     * @return 剩余偏移量
     */
    public final int getMaxFinalX() {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        return this.computeMaxFinalX(sliverContainer);
    }

    /**
     * 垂直剩余偏移量
     *
     * @return 剩余偏移量
     */
    public final int getMaxFinalY() {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        return this.computeMaxFinalY(sliverContainer);
    }

    public final int getFinalX(@IdRes int id) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        final View target = sliverContainer.findViewById(id);
        return this.getFinalX(target);
    }

    /**
     * 目标视图的水平坐标(X)
     *
     * @param target 目标视图
     * @return 坐标
     */
    public final int getFinalX(@Nullable View target) {
        return this.continueFinalX(target, 0);
    }

    public final int getFinalY(@IdRes int id) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        final View target = sliverContainer.findViewById(id);
        return this.getFinalY(target);
    }

    /**
     * 目标视图的垂直坐标(Y)
     *
     * @param target 目标视图
     * @return 坐标
     */
    public final int getFinalY(@Nullable View target) {
        return this.continueFinalY(target, 0);
    }

    public final int getScrollX(@IdRes int id) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        final View target = sliverContainer.findViewById(id);
        return this.getScrollX(target);
    }

    /**
     * 目标视图的水平偏移量
     *
     * @param target 目标视图
     * @return 偏移量
     */
    public final int getScrollX(@Nullable View target) {
        return this.continueScrollX(target, 0);
    }

    public final int getScrollY(@IdRes int id) {
        final SliverContainer sliverContainer = this.mSliverContainer;
        if (sliverContainer == null) {
            return 0;
        }
        final View target = sliverContainer.findViewById(id);
        return this.getScrollY(target);
    }

    /**
     * 目标视图的垂直偏移量
     *
     * @param target 目标视图
     * @return 偏移量
     */
    public final int getScrollY(@Nullable View target) {
        return this.continueScrollY(target, 0);
    }

    private int continueScrollX(@Nullable View target, int scrollX) {
        if (target == null) {
            return scrollX;
        }
        if (target instanceof ScrollingView) {
            final ScrollingView scrollingView = (ScrollingView) target;
            scrollX += scrollingView.computeHorizontalScrollOffset();
        }
        if (target == this.mSliverContainer) {
            return scrollX;
        }
        final ViewParent parent = target.getParent();
        if (parent instanceof View) {
            return this.continueScrollX((View) parent, scrollX);
        }
        return scrollX;
    }

    private int continueScrollY(@Nullable View target, int scrollY) {
        if (target == null) {
            return scrollY;
        }
        if (target instanceof ScrollingView) {
            final ScrollingView scrollingView = (ScrollingView) target;
            scrollY += scrollingView.computeVerticalScrollOffset();
        }
        if (target == this.mSliverContainer) {
            return scrollY;
        }
        final ViewParent parent = target.getParent();
        if (parent instanceof View) {
            return this.continueScrollY((View) parent, scrollY);
        }
        return scrollY;
    }

    private int continueFinalX(@Nullable View target, int upstreamFinalX) {
        if (target == null) {
            return upstreamFinalX;
        }
        final int finalX = target.getLeft() + upstreamFinalX;
        final ViewParent parent = target.getParent();
        if (parent == this.mSliverContainer) {
            return finalX;
        }
        if (parent instanceof View) {
            return this.continueFinalX((View) parent, finalX);
        }
        return finalX;
    }

    private int continueFinalY(@Nullable View target, int upstreamFinalY) {
        if (target == null) {
            return upstreamFinalY;
        }
        final int finalY = target.getTop() + upstreamFinalY;
        final ViewParent parent = target.getParent();
        if (parent == this.mSliverContainer) {
            return finalY;
        }
        if (parent instanceof View) {
            return this.continueFinalY((View) parent, finalY);
        }
        return finalY;
    }

    private int computeMaxFinalX(@Nullable SliverContainer sliverContainer) {
        final int finalRange = sliverContainer.computeHorizontalScrollRange();
        final int finalExtent = sliverContainer.computeHorizontalScrollExtent();
        final int finalOffset = sliverContainer.computeHorizontalScrollOffset();
        return finalRange - finalExtent - finalOffset;
    }

    private int computeMaxFinalY(@Nullable SliverContainer sliverContainer) {
        final int finalRange = sliverContainer.computeVerticalScrollRange();
        final int finalExtent = sliverContainer.computeVerticalScrollExtent();
        final int finalOffset = sliverContainer.computeVerticalScrollOffset();
        return finalRange - finalExtent - finalOffset;
    }

    private final class ComponentListener implements SliverContainer.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull SliverContainer sliverContainer,
                                         @SliverContainer.ScrollState int scrollState) {
            SliverSmoothHelper.this.onScrollStateChanged(sliverContainer, scrollState);
        }

        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            SliverSmoothHelper.this.onScrolled(sliverContainer, dx, dy);
        }
    }
}
