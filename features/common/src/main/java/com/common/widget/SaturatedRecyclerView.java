package com.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.framework.widget.expand.ExpandableRecyclerView;

/**
 * @Author create by Zhengzelong on 2024-03-22
 * @Email : 171905184@qq.com
 * @Description : 展示全部列表内容, 且不会触发滑动事件
 */
public class SaturatedRecyclerView extends ExpandableRecyclerView {
    public SaturatedRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public SaturatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaturatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(context, attrs, defStyleAttr, 0);
        }
        this.setLayoutManager(layoutManager);
        this.setNestedScrollingEnabled(false);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.clearFocus();
    }

    @Override
    protected void onMeasure(int parentWidthSpec, int parentHeightSpec) {
        parentHeightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(parentWidthSpec, parentHeightSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
