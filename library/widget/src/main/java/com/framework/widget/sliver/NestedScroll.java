package com.framework.widget.sliver;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent3;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface NestedScroll extends NestedScrollingParent3 {

    boolean onStartNestedScroll(@NonNull View child,
                                @NonNull View target,
                                @SliverCompat.ScrollAxis int scrollAxes,
                                @SliverCompat.ScrollType int scrollType);

    void onNestedScrollAccepted(@NonNull View child,
                                @NonNull View target,
                                @SliverCompat.ScrollAxis int scrollAxes,
                                @SliverCompat.ScrollType int scrollType);

    void onNestedPreScroll(@NonNull View target,
                           int dx,
                           int dy,
                           @NonNull int[] consumed,
                           @SliverCompat.ScrollType int scrollType);

    // NestedScrollingParent2

    /**
     * @deprecated
     */
    default void onNestedScroll(@NonNull View target,
                                int dxConsumed,
                                int dyConsumed,
                                int dxUnconsumed,
                                int dyUnconsumed,
                                @SliverCompat.ScrollType int scrollType) {
        final int[] consumed = new int[2];
        this.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollType, consumed);
    }

    // NestedScrollingParent3

    /**
     * @deprecated
     */
    default void onNestedScroll(@NonNull View target,
                                int dxConsumed,
                                int dyConsumed,
                                int dxUnconsumed,
                                int dyUnconsumed,
                                @SliverCompat.ScrollType int scrollType,
                                @NonNull int[] consumed) {
        this.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
    }

    void onNestedScroll(@NonNull View target,
                        int dxConsumed,
                        int dyConsumed,
                        int dxUnconsumed,
                        int dyUnconsumed,
                        @NonNull int[] consumed,
                        @SliverCompat.ScrollType int scrollType);

    boolean onNestedPreFling(@NonNull View target,
                             float velocityX,
                             float velocityY);

    boolean onNestedFling(@NonNull View target,
                          float velocityX,
                          float velocityY, boolean consumed);

    void onStopNestedScroll(@NonNull View target,
                            @SliverCompat.ScrollType int scrollType);

    @SliverCompat.ScrollAxis
    int getNestedScrollAxes();
}
