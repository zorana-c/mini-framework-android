package com.framework.widget.recycler.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.expand.ExpandableRecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-30
 * @Email : 171905184@qq.com
 * @Description : 简单的 RecyclerView
 * </pre>
 * 主要处理手势问题
 */
public class BannerRecyclerView extends ExpandableRecyclerView {
    private boolean mIsUserScrollEnabled = true;

    public BannerRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new BannerLayoutManager(context, attrs);
        }
        this.setLayoutManager(layoutManager);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            this.performEventStateChanged(event);
        }
        return super.dispatchTouchEvent(event);
    }

    private void performEventStateChanged(@NonNull MotionEvent event) {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        if (layoutManager instanceof BannerLayoutManager) {
            final BannerLayoutManager lm = (BannerLayoutManager) layoutManager;
            lm.performEventStateChanged(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.mIsUserScrollEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public boolean isUserScrollEnabled() {
        return this.mIsUserScrollEnabled;
    }

    public void setUserScrollEnabled(boolean enabled) {
        this.mIsUserScrollEnabled = enabled;
    }

    public void destroyPlay() {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager instanceof BannerLayoutManager) {
            ((BannerLayoutManager) layoutManager).destroyPlay();
        }
    }

    public void setLifecycle(@NonNull LifecycleOwner owner) {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager instanceof BannerLayoutManager) {
            ((BannerLayoutManager) layoutManager).setLifecycle(owner);
        }
    }

    public void setOrientation(@RecyclerView.Orientation int orientation) {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager instanceof BannerLayoutManager) {
            ((BannerLayoutManager) layoutManager).setOrientation(orientation);
        }
    }
}
