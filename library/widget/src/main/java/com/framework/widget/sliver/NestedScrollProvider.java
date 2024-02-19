package com.framework.widget.sliver;

import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface NestedScrollProvider extends NestedScrollingChild3 {

    boolean isNestedScrollingEnabled();

    void setNestedScrollingEnabled(boolean enabled);

    boolean startNestedScroll(@SliverCompat.ScrollAxis int scrollAxes);

    boolean startNestedScroll(@SliverCompat.ScrollAxis int scrollAxes,
                              @SliverCompat.ScrollType int scrollType);

    boolean dispatchNestedPreScroll(int dx,
                                    int dy,
                                    @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow);

    boolean dispatchNestedPreScroll(int dx,
                                    int dy,
                                    @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow,
                                    @SliverCompat.ScrollType int scrollType);

    boolean dispatchNestedScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow);

    boolean dispatchNestedScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow,
                                 @SliverCompat.ScrollType int scrollType);

    boolean dispatchNestedPreFling(float velocityX,
                                   float velocityY);

    boolean dispatchNestedFling(float velocityX,
                                float velocityY,
                                boolean consumed);

    void stopNestedScroll();

    void stopNestedScroll(@SliverCompat.ScrollType int scrollType);

    @Nullable
    ViewParent getNestedScrollingParent();

    @Nullable
    ViewParent getNestedScrollingParent(@SliverCompat.ScrollType int scrollType);

    boolean hasNestedScrollingParent();

    boolean hasNestedScrollingParent(@SliverCompat.ScrollType int scrollType);
}
