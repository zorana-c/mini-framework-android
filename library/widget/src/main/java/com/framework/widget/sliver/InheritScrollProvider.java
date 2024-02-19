package com.framework.widget.sliver;

import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface InheritScrollProvider {

    boolean isInheritScrollingEnabled();

    void setInheritScrollingEnabled(boolean enabled);

    boolean startInheritScroll(@SliverCompat.ScrollAxis int scrollAxes);

    boolean startInheritScroll(@SliverCompat.ScrollAxis int scrollAxes,
                               @SliverCompat.ScrollType int scrollType);

    boolean dispatchInheritPreScroll(int dx,
                                     int dy,
                                     @Nullable int[] consumed,
                                     @Nullable int[] offsetInWindow);

    boolean dispatchInheritPreScroll(int dx,
                                     int dy,
                                     @Nullable int[] consumed,
                                     @Nullable int[] offsetInWindow,
                                     @SliverCompat.ScrollType int scrollType);

    boolean dispatchInheritScroll(int dxConsumed,
                                  int dyConsumed,
                                  int dxUnconsumed,
                                  int dyUnconsumed,
                                  @Nullable int[] consumed,
                                  @Nullable int[] offsetInWindow);

    boolean dispatchInheritScroll(int dxConsumed,
                                  int dyConsumed,
                                  int dxUnconsumed,
                                  int dyUnconsumed,
                                  @Nullable int[] consumed,
                                  @Nullable int[] offsetInWindow,
                                  @SliverCompat.ScrollType int scrollType);

    boolean dispatchInheritPreFling(float velocityX,
                                    float velocityY);

    boolean dispatchInheritFling(float velocityX,
                                 float velocityY,
                                 boolean consumed);

    void stopInheritScroll();

    void stopInheritScroll(@SliverCompat.ScrollType int scrollType);

    @Nullable
    View getInheritScrollingChild();

    @Nullable
    View getInheritScrollingChild(@SliverCompat.ScrollType int scrollType);

    boolean hasInheritScrollingChild();

    boolean hasInheritScrollingChild(@SliverCompat.ScrollType int scrollType);
}
