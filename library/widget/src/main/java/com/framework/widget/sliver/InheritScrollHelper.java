package com.framework.widget.sliver;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-02-15
 * @Email : 171905184@qq.com
 * @Description :
 */
public class InheritScrollHelper {
    private final ViewGroup mParent;
    private final int[] mScrollConsumed = new int[2];

    private View mInheritScrollingChildTouch;
    private View mInheritScrollingChildNonTouch;
    private boolean mIsInheritScrollingEnabled;

    public InheritScrollHelper(@NonNull ViewGroup parent) {
        this.mParent = parent;
    }

    public boolean isInheritScrollingEnabled() {
        return this.mIsInheritScrollingEnabled;
    }

    public void setInheritScrollingEnabled(boolean enabled) {
        if (this.isInheritScrollingEnabled()) {
            SliverCompat.stopInheritScroll(this.mParent);
        }
        this.mIsInheritScrollingEnabled = enabled;
    }

    public boolean startInheritScroll(@SliverCompat.ScrollAxis int scrollAxes) {
        return this.startInheritScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    public boolean startInheritScroll(@SliverCompat.ScrollAxis int scrollAxes,
                                      @SliverCompat.ScrollType int scrollType) {
        if (this.hasInheritScrollingChild(scrollType)) {
            return true;
        }
        if (this.isInheritScrollingEnabled()) {
            final ViewGroup parent = this.mParent;
            View child;
            for (int index = parent.getChildCount() - 1; index >= 0; index--) {
                child = parent.getChildAt(index);
                if (this.startInheritScroll(child, scrollAxes, scrollType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startInheritScroll(@NonNull View child,
                                      @SliverCompat.ScrollAxis int scrollAxes,
                                      @SliverCompat.ScrollType int scrollType) {
        if (this.hasInheritScrollingChild(scrollType)) {
            return true;
        }
        if (this.isInheritScrollingEnabled()) {
            final ViewGroup parent = (ViewGroup) child.getParent();
            if (parent != this.mParent) {
                return false;
            }
            return this.startInheritScroll(child, parent, parent, scrollAxes, scrollType);
        }
        return false;
    }

    public boolean dispatchInheritPreScroll(int dx,
                                            int dy,
                                            @Nullable int[] consumed,
                                            @Nullable int[] offsetInWindow) {
        return this.dispatchInheritPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchInheritPreScroll(int dx,
                                            int dy,
                                            @Nullable int[] consumed,
                                            @Nullable int[] offsetInWindow,
                                            @SliverCompat.ScrollType int scrollType) {
        if (this.isInheritScrollingEnabled()) {
            final View child = this.getInheritScrollingChildForType(scrollType);
            if (child == null) {
                return false;
            }
            if (dx != 0 || dy != 0) {
                final ViewGroup parent = this.mParent;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    parent.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onInheritPreScroll(child, parent, dx, dy, consumed, scrollType);
                if (offsetInWindow != null) {
                    parent.getLocationInWindow(offsetInWindow);
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

    public boolean dispatchInheritScroll(int dxConsumed,
                                         int dyConsumed,
                                         int dxUnconsumed,
                                         int dyUnconsumed,
                                         @Nullable int[] consumed,
                                         @Nullable int[] offsetInWindow) {
        return this.dispatchInheritScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchInheritScroll(int dxConsumed,
                                         int dyConsumed,
                                         int dxUnconsumed,
                                         int dyUnconsumed,
                                         @Nullable int[] consumed,
                                         @Nullable int[] offsetInWindow,
                                         @SliverCompat.ScrollType int scrollType) {
        if (this.isInheritScrollingEnabled()) {
            final View child = this.getInheritScrollingChildForType(scrollType);
            if (child == null) {
                return false;
            }
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                final ViewGroup parent = this.mParent;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    parent.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onInheritScroll(child, parent, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
                if (offsetInWindow != null) {
                    parent.getLocationInWindow(offsetInWindow);
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

    public boolean dispatchInheritPreFling(float velocityX,
                                           float velocityY) {
        if (this.isInheritScrollingEnabled()) {
            final View child = this.getInheritScrollingChildForType(SliverCompat.TYPE_TOUCH);
            if (child == null) {
                return false;
            }
            return ViewChildCompat.onInheritPreFling(child, this.mParent, velocityX, velocityY);
        }
        return false;
    }

    public boolean dispatchInheritFling(float velocityX,
                                        float velocityY, boolean consumed) {
        if (this.isInheritScrollingEnabled()) {
            final View child = this.getInheritScrollingChildForType(SliverCompat.TYPE_TOUCH);
            if (child == null) {
                return false;
            }
            return ViewChildCompat.onInheritFling(child, this.mParent, velocityX, velocityY, consumed);
        }
        return false;
    }

    public void stopInheritScroll() {
        this.stopInheritScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopInheritScroll(@SliverCompat.ScrollType int scrollType) {
        final View child = this.getInheritScrollingChildForType(scrollType);
        if (child == null) {
            return;
        }
        ViewChildCompat.onStopInheritScroll(child, this.mParent, scrollType);
        this.setInheritScrollingChildForType(scrollType, null);
    }

    @Nullable
    public View getInheritScrollingChild() {
        return this.getInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    @Nullable
    public View getInheritScrollingChild(@SliverCompat.ScrollType int scrollType) {
        return this.getInheritScrollingChildForType(scrollType);
    }

    public boolean hasInheritScrollingChild() {
        return this.hasInheritScrollingChild(SliverCompat.TYPE_TOUCH);
    }

    public boolean hasInheritScrollingChild(@SliverCompat.ScrollType int scrollType) {
        return this.getInheritScrollingChildForType(scrollType) != null;
    }

    @Nullable
    private View getInheritScrollingChildForType(@SliverCompat.ScrollType int scrollType) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                return this.mInheritScrollingChildTouch;
            case SliverCompat.TYPE_NON_TOUCH:
                return this.mInheritScrollingChildNonTouch;
        }
        return null;
    }

    private void setInheritScrollingChildForType(@SliverCompat.ScrollType int scrollType,
                                                 @Nullable View child) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mInheritScrollingChildTouch = child;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mInheritScrollingChildNonTouch = child;
                break;
        }
    }

    private boolean startInheritScroll(@NonNull View child,
                                       @NonNull ViewGroup parent,
                                       @NonNull ViewGroup target,
                                       @SliverCompat.ScrollAxis int scrollAxes,
                                       @SliverCompat.ScrollType int scrollType) {
        if (ViewChildCompat.onStartInheritScroll(child, parent, target, scrollAxes, scrollType)) {
            this.setInheritScrollingChildForType(scrollType, child);
            ViewChildCompat.onInheritScrollAccepted(child, parent, target, scrollAxes, scrollType);
            return true;
        }
        if (child instanceof ViewGroup) {
            parent = (ViewGroup) child;
            for (int index = parent.getChildCount() - 1; index >= 0; index--) {
                child = parent.getChildAt(index);
                if (this.startInheritScroll(child, parent, target, scrollAxes, scrollType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
