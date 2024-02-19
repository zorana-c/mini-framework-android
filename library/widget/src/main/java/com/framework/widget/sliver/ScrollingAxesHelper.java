package com.framework.widget.sliver;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2023-02-17
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ScrollingAxesHelper {
    @SliverCompat.ScrollAxis
    private int mScrollingAxisTouch = SliverCompat.SCROLL_AXIS_NONE;
    @SliverCompat.ScrollAxis
    private int mScrollingAxisNonTouch = SliverCompat.SCROLL_AXIS_NONE;

    public ScrollingAxesHelper(@NonNull View view) {
    }

    public int getScrollAxes() {
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        scrollAxes |= this.mScrollingAxisTouch;
        scrollAxes |= this.mScrollingAxisNonTouch;
        return scrollAxes;
    }

    public void save(@SliverCompat.ScrollAxis int scrollAxes) {
        this.save(SliverCompat.TYPE_TOUCH, scrollAxes);
    }

    public void save(@SliverCompat.ScrollType int scrollType,
                     @SliverCompat.ScrollAxis int scrollAxes) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mScrollingAxisTouch = scrollAxes;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mScrollingAxisNonTouch = scrollAxes;
                break;
        }
    }

    public void clear() {
        this.clear(SliverCompat.TYPE_TOUCH);
    }

    public void clear(@SliverCompat.ScrollType int scrollType) {
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                this.mScrollingAxisTouch = SliverCompat.SCROLL_AXIS_NONE;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                this.mScrollingAxisNonTouch = SliverCompat.SCROLL_AXIS_NONE;
                break;
        }
    }

    public boolean hasScrollAxes() {
        return this.hasScrollAxes(SliverCompat.TYPE_TOUCH);
    }

    public boolean hasScrollAxes(@SliverCompat.ScrollType int scrollType) {
        int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
        switch (scrollType) {
            case SliverCompat.TYPE_TOUCH:
                scrollAxes = this.mScrollingAxisTouch;
                break;
            case SliverCompat.TYPE_NON_TOUCH:
                scrollAxes = this.mScrollingAxisNonTouch;
                break;
        }
        return scrollAxes != SliverCompat.SCROLL_AXIS_NONE;
    }
}
