package com.framework.widget.sliver;

import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-02-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverCompat {
    public static final int TYPE_TOUCH = ViewCompat.TYPE_TOUCH;
    public static final int TYPE_NON_TOUCH = ViewCompat.TYPE_NON_TOUCH;

    @IntDef({
            TYPE_TOUCH,
            TYPE_NON_TOUCH})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface ScrollType {
    }

    public static final int SCROLL_AXIS_NONE = ViewCompat.SCROLL_AXIS_NONE;
    public static final int SCROLL_AXIS_VERTICAL = ViewCompat.SCROLL_AXIS_VERTICAL;
    public static final int SCROLL_AXIS_HORIZONTAL = ViewCompat.SCROLL_AXIS_HORIZONTAL;

    @IntDef({
            SCROLL_AXIS_NONE,
            SCROLL_AXIS_VERTICAL,
            SCROLL_AXIS_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    public @interface ScrollAxis {
    }

    // ########## NestedScroll ##########

    public static boolean isNestedScrollingEnabled(@NonNull View view) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).isNestedScrollingEnabled();
        } else {
            return ViewCompat.isNestedScrollingEnabled(view);
        }
    }

    public static void setNestedScrollingEnabled(@NonNull View view, boolean enabled) {
        if (view instanceof NestedScrollProvider) {
            ((NestedScrollProvider) view).setNestedScrollingEnabled(enabled);
        } else {
            ViewCompat.setNestedScrollingEnabled(view, enabled);
        }
    }

    public static boolean startNestedScroll(@NonNull View view) {
        return startNestedScroll(view, TYPE_TOUCH);
    }

    public static boolean startNestedScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).startNestedScroll(scrollType);
        } else {
            return ViewCompat.startNestedScroll(view, scrollType);
        }
    }

    public static boolean dispatchNestedPreScroll(@NonNull View view,
                                                  int dx,
                                                  int dy,
                                                  @Nullable int[] consumed,
                                                  @Nullable int[] offsetInWindow) {
        return dispatchNestedPreScroll(view, dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchNestedPreScroll(@NonNull View view,
                                                  int dx,
                                                  int dy,
                                                  @Nullable int[] consumed,
                                                  @Nullable int[] offsetInWindow,
                                                  @ScrollType int scrollType) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
        } else {
            return ViewCompat.dispatchNestedPreScroll(view, dx, dy, consumed, offsetInWindow, scrollType);
        }
    }

    public static boolean dispatchNestedScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow) {
        return dispatchNestedScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchNestedScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow,
                                               @ScrollType int scrollType) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
        } else if (consumed == null) {
            return ViewCompat.dispatchNestedScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, scrollType);
        } else {
            ViewCompat.dispatchNestedScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, scrollType, consumed);
            return consumed[0] != 0 || consumed[1] != 0;
        }
    }

    public static boolean dispatchNestedPreFling(@NonNull View view,
                                                 float velocityX,
                                                 float velocityY) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).dispatchNestedPreFling(velocityX, velocityY);
        } else {
            return ViewCompat.dispatchNestedPreFling(view, velocityX, velocityY);
        }
    }

    public static boolean dispatchNestedFling(@NonNull View view,
                                              float velocityX,
                                              float velocityY, boolean consumed) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).dispatchNestedFling(velocityX, velocityY, consumed);
        } else {
            return ViewCompat.dispatchNestedFling(view, velocityX, velocityY, consumed);
        }
    }

    public static void stopNestedScroll(@NonNull View view) {
        stopNestedScroll(view, TYPE_TOUCH);
    }

    public static void stopNestedScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof NestedScrollProvider) {
            ((NestedScrollProvider) view).stopNestedScroll(scrollType);
        } else {
            ViewCompat.stopNestedScroll(view, scrollType);
        }
    }

    public static boolean hasNestedScrollingParent(@NonNull View view) {
        return hasNestedScrollingParent(view, TYPE_TOUCH);
    }

    public static boolean hasNestedScrollingParent(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof NestedScrollProvider) {
            return ((NestedScrollProvider) view).hasNestedScrollingParent(scrollType);
        } else {
            return ViewCompat.hasNestedScrollingParent(view, scrollType);
        }
    }

    // ########## InheritScroll ##########

    public static boolean isInheritScrollingEnabled(@NonNull View view) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).isInheritScrollingEnabled();
        }
        return false;
    }

    public static void setInheritScrollingEnabled(@NonNull View view, boolean enabled) {
        if (view instanceof InheritScrollProvider) {
            ((InheritScrollProvider) view).setInheritScrollingEnabled(enabled);
        }
    }

    public static boolean startInheritScroll(@NonNull View view) {
        return startInheritScroll(view, TYPE_TOUCH);
    }

    public static boolean startInheritScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).startInheritScroll(scrollType);
        }
        return false;
    }

    public static boolean dispatchInheritPreScroll(@NonNull View view,
                                                   int dx,
                                                   int dy,
                                                   @Nullable int[] consumed,
                                                   @Nullable int[] offsetInWindow) {
        return dispatchInheritPreScroll(view, dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchInheritPreScroll(@NonNull View view,
                                                   int dx,
                                                   int dy,
                                                   @Nullable int[] consumed,
                                                   @Nullable int[] offsetInWindow,
                                                   @ScrollType int scrollType) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
        }
        return false;
    }

    public static boolean dispatchInheritScroll(@NonNull View view,
                                                int dxConsumed,
                                                int dyConsumed,
                                                int dxUnconsumed,
                                                int dyUnconsumed,
                                                @Nullable int[] consumed,
                                                @Nullable int[] offsetInWindow) {
        return dispatchInheritScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchInheritScroll(@NonNull View view,
                                                int dxConsumed,
                                                int dyConsumed,
                                                int dxUnconsumed,
                                                int dyUnconsumed,
                                                @Nullable int[] consumed,
                                                @Nullable int[] offsetInWindow,
                                                @ScrollType int scrollType) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
        }
        return false;
    }

    public static boolean dispatchInheritPreFling(@NonNull View view,
                                                  float velocityX,
                                                  float velocityY) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).dispatchInheritPreFling(velocityX, velocityY);
        }
        return false;
    }

    public static boolean dispatchInheritFling(@NonNull View view,
                                               float velocityX,
                                               float velocityY, boolean consumed) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).dispatchInheritFling(velocityX, velocityY, consumed);
        }
        return false;
    }

    public static void stopInheritScroll(@NonNull View view) {
        stopInheritScroll(view, TYPE_TOUCH);
    }

    public static void stopInheritScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof InheritScrollProvider) {
            ((InheritScrollProvider) view).stopInheritScroll(scrollType);
        }
    }

    public static boolean hasInheritScrollingChild(@NonNull View view) {
        return hasInheritScrollingChild(view, TYPE_TOUCH);
    }

    public static boolean hasInheritScrollingChild(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof InheritScrollProvider) {
            return ((InheritScrollProvider) view).hasInheritScrollingChild(scrollType);
        }
        return false;
    }

    // ########## SliverScroll ##########

    public static boolean isSliverScrollingEnabled(@NonNull View view) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).isSliverScrollingEnabled();
        }
        return false;
    }

    public static void setSliverScrollingEnabled(@NonNull View view, boolean enabled) {
        if (view instanceof SliverScrollProvider) {
            ((SliverScrollProvider) view).setSliverScrollingEnabled(enabled);
        }
    }

    public static boolean startSliverScroll(@NonNull View view) {
        return startSliverScroll(view, TYPE_TOUCH);
    }

    public static boolean startSliverScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).startSliverScroll(scrollType);
        }
        return false;
    }

    public static boolean dispatchSliverPreScroll(@NonNull View view,
                                                  int dx,
                                                  int dy,
                                                  @Nullable int[] consumed,
                                                  @Nullable int[] offsetInWindow) {
        return dispatchSliverPreScroll(view, dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchSliverPreScroll(@NonNull View view,
                                                  int dx,
                                                  int dy,
                                                  @Nullable int[] consumed,
                                                  @Nullable int[] offsetInWindow,
                                                  @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).dispatchSliverPreScroll(dx, dy, consumed, offsetInWindow, scrollType);
        }
        return false;
    }

    public static boolean dispatchSliverScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow) {
        return dispatchSliverScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchSliverScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow,
                                               @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
        }
        return false;
    }

    public static boolean dispatchBounceScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow) {
        return dispatchBounceScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, TYPE_TOUCH);
    }

    public static boolean dispatchBounceScroll(@NonNull View view,
                                               int dxConsumed,
                                               int dyConsumed,
                                               int dxUnconsumed,
                                               int dyUnconsumed,
                                               @Nullable int[] consumed,
                                               @Nullable int[] offsetInWindow,
                                               @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, scrollType);
        }
        return false;
    }

    public static boolean dispatchSliverPreFling(@NonNull View view,
                                                 float velocityX,
                                                 float velocityY) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).dispatchSliverPreFling(velocityX, velocityY);
        }
        return false;
    }

    public static boolean dispatchSliverFling(@NonNull View view,
                                              float velocityX,
                                              float velocityY, boolean consumed) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).dispatchSliverFling(velocityX, velocityY, consumed);
        }
        return false;
    }

    public static void stopSliverScroll(@NonNull View view) {
        stopSliverScroll(view, TYPE_TOUCH);
    }

    public static void stopSliverScroll(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            ((SliverScrollProvider) view).stopSliverScroll(scrollType);
        }
    }

    public static boolean hasSliverScrolling(@NonNull View view) {
        return hasSliverScrolling(view, TYPE_TOUCH);
    }

    public static boolean hasSliverScrolling(@NonNull View view, @ScrollType int scrollType) {
        if (view instanceof SliverScrollProvider) {
            return ((SliverScrollProvider) view).hasSliverScrolling(scrollType);
        }
        return false;
    }
}
