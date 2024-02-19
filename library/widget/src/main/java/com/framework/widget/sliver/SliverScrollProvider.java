package com.framework.widget.sliver;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface SliverScrollProvider {

    boolean isSliverScrollingEnabled();

    void setSliverScrollingEnabled(boolean enabled);

    boolean startSliverScroll(@SliverCompat.ScrollAxis int scrollAxes);

    boolean startSliverScroll(@SliverCompat.ScrollAxis int scrollAxes,
                              @SliverCompat.ScrollType int scrollType);

    boolean dispatchSliverPreScroll(int dx,
                                    int dy,
                                    @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow);

    boolean dispatchSliverPreScroll(int dx,
                                    int dy,
                                    @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow,
                                    @SliverCompat.ScrollType int scrollType);

    boolean dispatchSliverScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow);

    boolean dispatchSliverScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow,
                                 @SliverCompat.ScrollType int scrollType);

    boolean dispatchBounceScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow);

    boolean dispatchBounceScroll(int dxConsumed,
                                 int dyConsumed,
                                 int dxUnconsumed,
                                 int dyUnconsumed,
                                 @Nullable int[] consumed,
                                 @Nullable int[] offsetInWindow,
                                 @SliverCompat.ScrollType int scrollType);

    boolean dispatchSliverPreFling(float velocityX,
                                   float velocityY);

    boolean dispatchSliverFling(float velocityX,
                                float velocityY, boolean consumed);

    void stopSliverScroll();

    void stopSliverScroll(@SliverCompat.ScrollType int scrollType);

    boolean hasSliverScrolling();

    boolean hasSliverScrolling(@SliverCompat.ScrollType int scrollType);
}
