package com.framework.widget.sliver;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public class NestedScrollHelper {
    private final View mView;
    private final int[] mScrollConsumed = new int[2];

    private ViewParent mNestedScrollingParentTouch;
    private ViewParent mNestedScrollingParentNonTouch;
    private boolean mIsNestedScrollingEnabled;

    public NestedScrollHelper(@NonNull View view) {
        this.mView = view;
    }

    public boolean isNestedScrollingEnabled() {
        return this.mIsNestedScrollingEnabled;
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        if (this.isNestedScrollingEnabled()) {
            SliverCompat.stopNestedScroll(this.mView);
        }
        this.mIsNestedScrollingEnabled = enabled;
    }

    public boolean startNestedScroll(@SliverCompat.ScrollAxis int scrollAxes) {
        return this.startNestedScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    public boolean startNestedScroll(@SliverCompat.ScrollAxis int scrollAxes,
                                     @SliverCompat.ScrollType int scrollType) {
        if (this.hasNestedScrollingParent(scrollType)) {
            return true;
        }
        if (this.isNestedScrollingEnabled()) {
            final View target = this.mView;
            View child = target;
            ViewParent parent = target.getParent();
            while (parent != null) {
                if (ViewChildCompat.onStartNestedScroll(parent, child, target, scrollAxes, scrollType)) {
                    this.setNestedScrollingParentForType(scrollType, parent);
                    ViewChildCompat.onNestedScrollAccepted(parent, child, target, scrollAxes, scrollType);
                    return true;
                }
                if (parent instanceof View) {
                    child = (View) parent;
                }
                parent = parent.getParent();
            }
        }
        return false;
    }

    public boolean dispatchNestedPreScroll(int dx,
                                           int dy,
                                           @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow) {
        return this.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchNestedPreScroll(int dx,
                                           int dy,
                                           @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow,
                                           @SliverCompat.ScrollType int scrollType) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent parent = this.getNestedScrollingParentForType(scrollType);
            if (parent == null) {
                return false;
            }
            if (dx != 0 || dy != 0) {
                final View target = this.mView;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    target.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onNestedPreScroll(parent, target, dx, dy, consumed, scrollType);
                if (offsetInWindow != null) {
                    target.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                return consumed[0] != 0 || consumed[1] != 0;
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    public boolean dispatchNestedScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow) {
        return this.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchNestedScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow,
                                        @SliverCompat.ScrollType int scrollType) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent parent = this.getNestedScrollingParentForType(scrollType);
            if (parent == null) {
                return false;
            }
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                final View target = this.mView;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    target.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onNestedScroll(parent, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
                if (offsetInWindow != null) {
                    target.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                return consumed[0] != 0 || consumed[1] != 0;
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    public boolean dispatchNestedPreFling(float velocityX,
                                          float velocityY) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent parent = this.getNestedScrollingParentForType(SliverCompat.TYPE_TOUCH);
            if (parent == null) {
                return false;
            }
            return ViewChildCompat.onNestedPreFling(parent, this.mView, velocityX, velocityY);
        }
        return false;
    }

    public boolean dispatchNestedFling(float velocityX,
                                       float velocityY, boolean consumed) {
        if (this.isNestedScrollingEnabled()) {
            final ViewParent parent = this.getNestedScrollingParentForType(SliverCompat.TYPE_TOUCH);
            if (parent == null) {
                return false;
            }
            return ViewChildCompat.onNestedFling(parent, this.mView, velocityX, velocityY, consumed);
        }
        return false;
    }

    public void stopNestedScroll() {
        this.stopNestedScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopNestedScroll(@SliverCompat.ScrollType int scrollType) {
        final ViewParent parent = this.getNestedScrollingParentForType(scrollType);
        if (parent == null) {
            return;
        }
        ViewChildCompat.onStopNestedScroll(parent, this.mView, scrollType);
        this.setNestedScrollingParentForType(scrollType, null);
    }

    @Nullable
    public ViewParent getNestedScrollingParent() {
        return this.getNestedScrollingParent(SliverCompat.TYPE_TOUCH);
    }

    @Nullable
    public ViewParent getNestedScrollingParent(@SliverCompat.ScrollType int scrollType) {
        return this.getNestedScrollingParentForType(scrollType);
    }

    public boolean hasNestedScrollingParent() {
        return this.hasNestedScrollingParent(SliverCompat.TYPE_TOUCH);
    }

    public boolean hasNestedScrollingParent(@SliverCompat.ScrollType int scrollType) {
        return this.getNestedScrollingParentForType(scrollType) != null;
    }

    @Nullable
    private ViewParent getNestedScrollingParentForType(@SliverCompat.ScrollType int scrollType) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                return this.mNestedScrollingParentTouch;
            case SliverCompat.TYPE_NON_TOUCH:
                return this.mNestedScrollingParentNonTouch;
        }
        return null;
    }

    private void setNestedScrollingParentForType(@SliverCompat.ScrollType int scrollType,
                                                 @Nullable ViewParent parent) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mNestedScrollingParentTouch = parent;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mNestedScrollingParentNonTouch = parent;
                break;
        }
    }
}
