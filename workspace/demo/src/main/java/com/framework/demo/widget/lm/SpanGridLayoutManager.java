package com.framework.demo.widget.lm;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.demo.R;
import com.framework.widget.expand.compat.GridLayoutManager;

/**
 * @Author create by Zhengzelong on 2023-07-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SpanGridLayoutManager extends GridLayoutManager
        implements RecyclerView.OnChildAttachStateChangeListener {
    private float itemSpanRatio;

    public SpanGridLayoutManager(@NonNull Context context) {
        this(context, 2);
    }

    public SpanGridLayoutManager(@NonNull Context context, int spanCount) {
        this(context, spanCount, RecyclerView.VERTICAL);
    }

    public SpanGridLayoutManager(@NonNull Context context, int spanCount, int orientation) {
        this(context, spanCount, orientation, false);
    }

    public SpanGridLayoutManager(@NonNull Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        this.init(context, null);
    }

    public SpanGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpanGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SpanGridLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewCompat);
        try {
            this.itemSpanRatio = ta.getFloat(R.styleable.RecyclerViewCompat_itemSpanRatio, 0.f);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        recyclerView.addOnChildAttachStateChangeListener(this);
    }

    @Override
    public void onDetachedFromWindow(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        recyclerView.removeOnChildAttachStateChangeListener(this);
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View itemView) {
        final int ori = this.getOrientation();
        final float w = this.getContentWidth();
        final float h = this.getContentHeight();
        final float isr = this.itemSpanRatio;

        if (isr <= 0.f) {
            return;
        }
        final ViewGroup.LayoutParams lp;
        lp = itemView.getLayoutParams();

        final int width = (int) (w * isr);
        final int height = (int) (h * isr);
        boolean needsReSet = false;

        if (RecyclerView.HORIZONTAL == ori) {
            if (lp.width != width) {
                lp.width = width;
                needsReSet = true;
            }
        } else {
            if (lp.height != height) {
                lp.height = height;
                needsReSet = true;
            }
        }
        if (needsReSet) {
            itemView.setLayoutParams(lp);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View itemView) {
        // no-op
    }

    public final int getContentWidth() {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }

    public final int getContentHeight() {
        return this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
    }

    @FloatRange(from = 0.f, to = 1.f)
    public float getItemSpanRatio() {
        return this.itemSpanRatio;
    }

    public void setItemSpanRatio(@FloatRange(from = 0.f, to = 1.f) float itemSpanRatio) {
        this.itemSpanRatio = itemSpanRatio;
    }
}
