package com.framework.widget.sliver;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.core.view.ViewParentCompat;

/**
 * @Author create by Zhengzelong on 2023-02-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class ViewChildCompat {

    private ViewChildCompat() {
    }

    // ########## NestedScroll ##########

    public static boolean onStartNestedScroll(@NonNull ViewParent parent,
                                              @NonNull View child,
                                              @NonNull View target,
                                              @SliverCompat.ScrollAxis int scrollAxes,
                                              @SliverCompat.ScrollType int scrollType) {
        if (parent instanceof NestedScroll) {
            return ((NestedScroll) parent).onStartNestedScroll(child, target, scrollAxes, scrollType);
        }
        return ViewParentCompat.onStartNestedScroll(parent, child, target, scrollAxes, scrollType);
    }

    public static void onNestedScrollAccepted(@NonNull ViewParent parent,
                                              @NonNull View child,
                                              @NonNull View target,
                                              @SliverCompat.ScrollAxis int scrollAxes,
                                              @SliverCompat.ScrollType int scrollType) {
        if (parent instanceof NestedScroll) {
            ((NestedScroll) parent).onNestedScrollAccepted(child, target, scrollAxes, scrollType);
        } else {
            ViewParentCompat.onNestedScrollAccepted(parent, child, target, scrollAxes, scrollType);
        }
    }

    public static void onNestedPreScroll(@NonNull ViewParent parent,
                                         @NonNull View target,
                                         int dx,
                                         int dy,
                                         @NonNull int[] consumed,
                                         @SliverCompat.ScrollType int scrollType) {
        if (parent instanceof NestedScroll) {
            ((NestedScroll) parent).onNestedPreScroll(target, dx, dy, consumed, scrollType);
        } else {
            ViewParentCompat.onNestedPreScroll(parent, target, dx, dy, consumed, scrollType);
        }
    }

    public static void onNestedScroll(@NonNull ViewParent parent,
                                      @NonNull View target,
                                      int dxConsumed,
                                      int dyConsumed,
                                      int dxUnconsumed,
                                      int dyUnconsumed,
                                      @NonNull int[] consumed,
                                      @SliverCompat.ScrollType int scrollType) {
        if (parent instanceof NestedScroll) {
            ((NestedScroll) parent).onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
        } else {
            ViewParentCompat.onNestedScroll(parent, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, scrollType, consumed);
        }
    }

    public static boolean onNestedPreFling(@NonNull ViewParent parent,
                                           @NonNull View target,
                                           float velocityX,
                                           float velocityY) {
        if (parent instanceof NestedScroll) {
            return ((NestedScroll) parent).onNestedPreFling(target, velocityX, velocityY);
        }
        return ViewParentCompat.onNestedPreFling(parent, target, velocityX, velocityY);
    }

    public static boolean onNestedFling(@NonNull ViewParent parent,
                                        @NonNull View target,
                                        float velocityX,
                                        float velocityY, boolean consumed) {
        if (parent instanceof NestedScroll) {
            return ((NestedScroll) parent).onNestedFling(target, velocityX, velocityY, consumed);
        }
        return ViewParentCompat.onNestedFling(parent, target, velocityX, velocityY, consumed);
    }

    public static void onStopNestedScroll(@NonNull ViewParent parent,
                                          @NonNull View target,
                                          @SliverCompat.ScrollType int scrollType) {
        if (parent instanceof NestedScroll) {
            ((NestedScroll) parent).onStopNestedScroll(target, scrollType);
        } else {
            ViewParentCompat.onStopNestedScroll(parent, target, scrollType);
        }
    }

    // ########## InheritScroll ##########

    public static boolean onStartInheritScroll(@NonNull View child,
                                               @NonNull ViewParent parent,
                                               @NonNull ViewParent target,
                                               @SliverCompat.ScrollAxis int scrollAxes,
                                               @SliverCompat.ScrollType int scrollType) {
        if (child instanceof InheritScroll) {
            return ((InheritScroll) child).onStartInheritScroll(parent, target, scrollAxes, scrollType);
        }
        return false;
    }

    public static void onInheritScrollAccepted(@NonNull View child,
                                               @NonNull ViewParent parent,
                                               @NonNull ViewParent target,
                                               @SliverCompat.ScrollAxis int scrollAxes,
                                               @SliverCompat.ScrollType int scrollType) {
        if (child instanceof InheritScroll) {
            ((InheritScroll) child).onInheritScrollAccepted(parent, target, scrollAxes, scrollType);
        }
    }

    public static void onInheritPreScroll(@NonNull View child,
                                          @NonNull ViewParent target,
                                          int dx,
                                          int dy,
                                          @NonNull int[] consumed,
                                          @SliverCompat.ScrollType int scrollType) {
        if (child instanceof InheritScroll) {
            ((InheritScroll) child).onInheritPreScroll(target, dx, dy, consumed, scrollType);
        }
    }

    public static void onInheritScroll(@NonNull View child,
                                       @NonNull ViewParent target,
                                       int dxConsumed,
                                       int dyConsumed,
                                       int dxUnconsumed,
                                       int dyUnconsumed,
                                       @NonNull int[] consumed,
                                       @SliverCompat.ScrollType int scrollType) {
        if (child instanceof InheritScroll) {
            ((InheritScroll) child).onInheritScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
        }
    }

    public static boolean onInheritPreFling(@NonNull View child,
                                            @NonNull ViewParent target,
                                            float velocityX,
                                            float velocityY) {
        if (child instanceof InheritScroll) {
            return ((InheritScroll) child).onInheritPreFling(target, velocityX, velocityY);
        }
        return false;
    }

    public static boolean onInheritFling(@NonNull View child,
                                         @NonNull ViewParent target,
                                         float velocityX,
                                         float velocityY, boolean consumed) {
        if (child instanceof InheritScroll) {
            return ((InheritScroll) child).onInheritFling(target, velocityX, velocityY, consumed);
        }
        return false;
    }

    public static void onStopInheritScroll(@NonNull View child,
                                           @NonNull ViewParent target,
                                           @SliverCompat.ScrollType int scrollType) {
        if (child instanceof InheritScroll) {
            ((InheritScroll) child).onStopInheritScroll(target, scrollType);
        }
    }

    // ########## SliverScroll ##########

    public static boolean onStartSliverScroll(@NonNull View child,
                                              @SliverCompat.ScrollAxis int scrollAxes,
                                              @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            return ((SliverScroll) child).onStartSliverScroll(scrollAxes, scrollType);
        }
        return false;
    }

    public static void onSliverScrollAccepted(@NonNull View child,
                                              @SliverCompat.ScrollAxis int scrollAxes,
                                              @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            ((SliverScroll) child).onSliverScrollAccepted(scrollAxes, scrollType);
        }
    }

    public static void onSliverPreScroll(@NonNull View child,
                                         int dx,
                                         int dy,
                                         @NonNull int[] consumed,
                                         @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            ((SliverScroll) child).onSliverPreScroll(dx, dy, consumed, scrollType);
        }
    }

    public static void onSliverScroll(@NonNull View child,
                                      int dxConsumed,
                                      int dyConsumed,
                                      int dxUnconsumed,
                                      int dyUnconsumed,
                                      @NonNull int[] consumed,
                                      @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            ((SliverScroll) child).onSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
        }
    }

    public static void onBounceScroll(@NonNull View child,
                                      int dxConsumed,
                                      int dyConsumed,
                                      int dxUnconsumed,
                                      int dyUnconsumed,
                                      @NonNull int[] consumed,
                                      @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            ((SliverScroll) child).onBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
        }
    }

    public static boolean onSliverPreFling(@NonNull View child,
                                           float velocityX,
                                           float velocityY) {
        if (child instanceof SliverScroll) {
            return ((SliverScroll) child).onSliverPreFling(velocityX, velocityY);
        }
        return false;
    }

    public static boolean onSliverFling(@NonNull View child,
                                        float velocityX,
                                        float velocityY, boolean consumed) {
        if (child instanceof SliverScroll) {
            return ((SliverScroll) child).onSliverFling(velocityX, velocityY, consumed);
        }
        return false;
    }

    public static void onStopSliverScroll(@NonNull View child,
                                          @SliverCompat.ScrollType int scrollType) {
        if (child instanceof SliverScroll) {
            ((SliverScroll) child).onStopSliverScroll(scrollType);
        }
    }
}
