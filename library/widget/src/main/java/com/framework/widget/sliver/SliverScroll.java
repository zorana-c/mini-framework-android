package com.framework.widget.sliver;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2023-02-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface SliverScroll {

    boolean onStartSliverScroll(@SliverCompat.ScrollAxis int scrollAxes,
                                @SliverCompat.ScrollType int scrollType);

    void onSliverScrollAccepted(@SliverCompat.ScrollAxis int scrollAxes,
                                @SliverCompat.ScrollType int scrollType);

    void onSliverPreScroll(int dx,
                           int dy,
                           @NonNull int[] consumed,
                           @SliverCompat.ScrollType int scrollType);

    void onSliverScroll(int dxConsumed,
                        int dyConsumed,
                        int dxUnconsumed,
                        int dyUnconsumed,
                        @NonNull int[] consumed,
                        @SliverCompat.ScrollType int scrollType);

    void onBounceScroll(int dxConsumed,
                        int dyConsumed,
                        int dxUnconsumed,
                        int dyUnconsumed,
                        @NonNull int[] consumed,
                        @SliverCompat.ScrollType int scrollType);

    boolean onSliverPreFling(float velocityX,
                             float velocityY);

    boolean onSliverFling(float velocityX,
                          float velocityY, boolean consumed);

    void onStopSliverScroll(@SliverCompat.ScrollType int scrollType);

    @SliverCompat.ScrollAxis
    int getSliverScrollAxes();
}
