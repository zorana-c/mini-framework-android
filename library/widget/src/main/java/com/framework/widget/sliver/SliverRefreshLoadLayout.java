package com.framework.widget.sliver;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author create by Zhengzelong on 2023-03-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverRefreshLoadLayout extends FrameLayout implements
        SliverRefreshLayout.RefreshInterfaces<SliverRefreshLayout> {
    /**
     * 默认滑动样式
     */
    public static final int SCROLL_STYLE_NONE = -1;
    /**
     * 先迟钝滑动，后紧跟随在后面滑动
     */
    public static final int SCROLL_STYLE_FAST = 0;
    /**
     * 先不滑动，后紧跟随在后面滑动
     */
    public static final int SCROLL_STYLE_SLOW = 1;
    /**
     * 直紧跟随在后面滑动
     */
    public static final int SCROLL_STYLE_FOLLOW = 2;

    @IntDef({SCROLL_STYLE_NONE,
            SCROLL_STYLE_FAST,
            SCROLL_STYLE_SLOW,
            SCROLL_STYLE_FOLLOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollStyle {
    }

    @ScrollStyle
    private int mScrollStyle = SCROLL_STYLE_NONE;

    public SliverRefreshLoadLayout(@NonNull Context context) {
        this(context, null);
    }

    public SliverRefreshLoadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverRefreshLoadLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMeasureAllChildren(true);
    }

    @Override
    public int getRefreshDecorSize(@NonNull SliverRefreshLayout parent) {
        if (parent.canScrollVertically()) {
            return parent.getDecoratedMeasuredHeight(this);
        } else {
            return parent.getDecoratedMeasuredWidth(this);
        }
    }

    @Override
    public void onRefreshScrolled(@NonNull SliverRefreshLayout parent, int dx, int dy) {
        final int locate = parent.getChildLocate(this);
        float scrollX = -parent.getExtraOffsetX();
        float scrollY = -parent.getExtraOffsetY();

        final float width = this.getMeasuredWidth();
        final float height = this.getMeasuredHeight();
        int scrollStyle = this.mScrollStyle;
        if (scrollStyle == SCROLL_STYLE_NONE) {
            scrollStyle = SCROLL_STYLE_FOLLOW;
        }

        // style 1.
        if (SCROLL_STYLE_FOLLOW == scrollStyle) {
            scrollX = Math.round(width * locate + scrollX);
            scrollY = Math.round(height * locate + scrollY);
        }

        // style 2.
        if (SCROLL_STYLE_SLOW == scrollStyle) {
            scrollX = Math.round(Math.min(0, scrollX * locate + width) * locate);
            scrollY = Math.round(Math.min(0, scrollY * locate + height) * locate);
        }

        // style 3.
        if (SCROLL_STYLE_FAST == scrollStyle) {
            final float transformPosX = width / 2.F * (1.F + scrollX * locate / width);
            final float transformPosY = height / 2.F * (1.F + scrollY * locate / height);
            scrollX = Math.round(Math.min(transformPosX, scrollX * locate + width) * locate);
            scrollY = Math.round(Math.min(transformPosY, scrollY * locate + height) * locate);
        }

        if (parent.canScrollVertically()) {
            this.setTranslationY(scrollY);
        } else {
            this.setTranslationX(scrollX);
        }
    }

    @Override
    public void onRefreshStateChanged(@NonNull SliverRefreshLayout parent, int refreshState) {
        if (SliverRefreshLayout.REFRESH_STATE_NONE == refreshState) {
            if (this.getVisibility() != View.GONE) {
                this.setVisibility(View.GONE);
            }
        } else {
            if (this.getVisibility() != View.VISIBLE) {
                this.setVisibility(View.VISIBLE);
            }
        }
    }

    @ScrollStyle
    public int getScrollStyle() {
        return this.mScrollStyle;
    }

    public void setScrollStyle(@ScrollStyle int scrollStyle) {
        this.mScrollStyle = scrollStyle;
    }
}
