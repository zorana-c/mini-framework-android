package com.framework.widget.sliver;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-02-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverScrollHelper {
    private final View mView;
    private final int[] mScrollConsumed = new int[2];

    private boolean mSliverScrollingTouch;
    private boolean mSliverScrollingNonTouch;
    private boolean mPendingScrollingTouch;
    private boolean mPendingScrollingNonTouch;
    private boolean mIsSliverScrollingEnabled;

    public SliverScrollHelper(@NonNull View view) {
        this.mView = view;
    }

    public boolean isSliverScrollingEnabled() {
        return this.mIsSliverScrollingEnabled;
    }

    public void setSliverScrollingEnabled(boolean enabled) {
        if (this.isSliverScrollingEnabled()) {
            SliverCompat.stopSliverScroll(this.mView);
        }
        this.mIsSliverScrollingEnabled = enabled;
    }

    public boolean startSliverScroll(@SliverCompat.ScrollAxis int scrollAxes) {
        return this.startSliverScroll(scrollAxes, SliverCompat.TYPE_TOUCH);
    }

    public boolean startSliverScroll(@SliverCompat.ScrollAxis int scrollAxes,
                                     @SliverCompat.ScrollType int scrollType) {
        if (this.hasSliverScrolling(scrollType)) {
            return true;
        }
        if (this.isSliverScrollingEnabled()) {
            if (scrollAxes == SliverCompat.SCROLL_AXIS_NONE) {
                return false;
            }
            final View view = this.mView;
            if (ViewChildCompat.onStartSliverScroll(view, scrollAxes, scrollType)) {
                this.setSliverScrollingForType(scrollType, true);
                ViewChildCompat.onSliverScrollAccepted(view, scrollAxes, scrollType);
                return true;
            }
        }
        return false;
    }

    public boolean dispatchSliverPreScroll(int dx,
                                           int dy,
                                           @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow) {
        return this.dispatchSliverPreScroll(dx, dy, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchSliverPreScroll(int dx,
                                           int dy,
                                           @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow,
                                           @SliverCompat.ScrollType int scrollType) {
        if (this.isSliverScrollingEnabled()) {
            if (!this.hasSliverScrolling(scrollType)) {
                return false;
            }
            if (dx != 0 || dy != 0) {
                final View view = this.mView;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onSliverPreScroll(view, dx, dy, consumed, scrollType);
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
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

    public boolean dispatchSliverScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow) {
        return this.dispatchSliverScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchSliverScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow,
                                        @SliverCompat.ScrollType int scrollType) {
        if (this.isSliverScrollingEnabled()) {
            if (!this.hasSliverScrolling(scrollType)) {
                return false;
            }
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                final View view = this.mView;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onSliverScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
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

    public boolean dispatchBounceScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow) {
        return this.dispatchBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, offsetInWindow, SliverCompat.TYPE_TOUCH);
    }

    public boolean dispatchBounceScroll(int dxConsumed,
                                        int dyConsumed,
                                        int dxUnconsumed,
                                        int dyUnconsumed,
                                        @Nullable int[] consumed,
                                        @Nullable int[] offsetInWindow,
                                        @SliverCompat.ScrollType int scrollType) {
        if (this.isSliverScrollingEnabled()) {
            if (!this.hasSliverScrolling(scrollType)) {
                return false;
            }
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                final View view = this.mView;
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }
                if (consumed == null) {
                    consumed = this.mScrollConsumed;
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewChildCompat.onBounceScroll(view, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
                if (offsetInWindow != null) {
                    view.getLocationInWindow(offsetInWindow);
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

    public boolean dispatchSliverPreFling(float velocityX,
                                          float velocityY) {
        if (this.isSliverScrollingEnabled()) {
            if (!this.hasSliverScrolling(SliverCompat.TYPE_TOUCH)) {
                return false;
            }
            return ViewChildCompat.onSliverPreFling(this.mView, velocityX, velocityY);
        }
        return false;
    }

    public boolean dispatchSliverFling(float velocityX,
                                       float velocityY, boolean consumed) {
        if (this.isSliverScrollingEnabled()) {
            if (!this.hasSliverScrolling(SliverCompat.TYPE_TOUCH)) {
                return false;
            }
            return ViewChildCompat.onSliverFling(this.mView, velocityX, velocityY, consumed);
        }
        return false;
    }

    public void stopSliverScroll() {
        this.stopSliverScroll(SliverCompat.TYPE_TOUCH);
    }

    public void stopSliverScroll(@SliverCompat.ScrollType int scrollType) {
        if (!this.hasSliverScrolling(scrollType)) {
            return;
        }
        this.setPendingScrollingForType(scrollType, false);
        ViewChildCompat.onStopSliverScroll(this.mView, scrollType);
        if (this.getPendingScrollingForType(scrollType)) {
            return;
        }
        this.setSliverScrollingForType(scrollType, false);
    }

    public boolean hasSliverScrolling() {
        return this.hasSliverScrolling(SliverCompat.TYPE_TOUCH);
    }

    public boolean hasSliverScrolling(@SliverCompat.ScrollType int scrollType) {
        return this.getSliverScrollingForType(scrollType);
    }

    private boolean getSliverScrollingForType(@SliverCompat.ScrollType int scrollType) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                return this.mSliverScrollingTouch;
            case SliverCompat.TYPE_NON_TOUCH:
                return this.mSliverScrollingNonTouch;
        }
        return false;
    }

    private void setSliverScrollingForType(@SliverCompat.ScrollType int scrollType,
                                           boolean scrolling) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mSliverScrollingTouch = scrolling;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mSliverScrollingNonTouch = scrolling;
                break;
        }
        this.setPendingScrollingForType(scrollType, scrolling);
    }

    private boolean getPendingScrollingForType(@SliverCompat.ScrollType int scrollType) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                return this.mPendingScrollingTouch;
            case SliverCompat.TYPE_NON_TOUCH:
                return this.mPendingScrollingNonTouch;
        }
        return false;
    }

    private void setPendingScrollingForType(@SliverCompat.ScrollType int scrollType,
                                            boolean scrolling) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mPendingScrollingTouch = scrolling;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mPendingScrollingNonTouch = scrolling;
                break;
        }
    }
}
