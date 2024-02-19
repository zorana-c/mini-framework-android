package com.framework.widget.sliver;

import android.view.ViewParent;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface InheritScroll {

    boolean onStartInheritScroll(@NonNull ViewParent parent,
                                 @NonNull ViewParent target,
                                 @SliverCompat.ScrollAxis int scrollAxes,
                                 @SliverCompat.ScrollType int scrollType);

    void onInheritScrollAccepted(@NonNull ViewParent parent,
                                 @NonNull ViewParent target,
                                 @SliverCompat.ScrollAxis int scrollAxes,
                                 @SliverCompat.ScrollType int scrollType);

    void onInheritPreScroll(@NonNull ViewParent target,
                            int dx,
                            int dy,
                            @NonNull int[] consumed,
                            @SliverCompat.ScrollType int scrollType);

    void onInheritScroll(@NonNull ViewParent target,
                         int dxConsumed,
                         int dyConsumed,
                         int dxUnconsumed,
                         int dyUnconsumed,
                         @NonNull int[] consumed,
                         @SliverCompat.ScrollType int scrollType);

    boolean onInheritPreFling(@NonNull ViewParent target,
                              float velocityX,
                              float velocityY);

    boolean onInheritFling(@NonNull ViewParent target,
                           float velocityX,
                           float velocityY, boolean consumed);

    void onStopInheritScroll(@NonNull ViewParent target,
                             @SliverCompat.ScrollType int scrollType);

    @SliverCompat.ScrollAxis
    int getInheritScrollAxes();
}
