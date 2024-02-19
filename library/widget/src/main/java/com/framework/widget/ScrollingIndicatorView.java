package com.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.view.ScrollingView;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.sliver.SliverContainer;

/**
 * @Author create by Zhengzelong on 2021/4/28
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ScrollingIndicatorView extends View {
    private static final float DEF_RATIO = 0.7f;
    private static final float DEF_RADIUS = 4.5f; // ==> height = 3dp
    @NonNull
    private final RectF mIndicatorRectF;
    @NonNull
    private final Paint mIndicatorPaint;
    @NonNull
    private final RectF mBackgroundRectF;
    @NonNull
    private final Paint mBackgroundPaint;

    private float mIndicatorRatio;
    private float mIndicatorRadius;

    @Nullable
    private ScrollingView mScrollingView;
    @Nullable
    private ComponentListener mComponentListener;
    @Nullable
    private ComponentListener2 mComponentListener2;

    public ScrollingIndicatorView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollingIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollingIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollingIndicatorView);
        final int indicatorColor;
        final int backgroundColor;
        try {
            indicatorColor = ta.getColor(R.styleable.ScrollingIndicatorView_sivIndicatorColor, Color.WHITE);
            backgroundColor = ta.getColor(R.styleable.ScrollingIndicatorView_sivIndicatorBackgroundColor, Color.GRAY);
            this.mIndicatorRatio = ta.getFloat(R.styleable.ScrollingIndicatorView_sivIndicatorRatio, DEF_RATIO);
            this.mIndicatorRadius = ta.getDimension(R.styleable.ScrollingIndicatorView_sivIndicatorRadius, DEF_RADIUS);
        } finally {
            ta.recycle();
        }
        this.mIndicatorRectF = new RectF();
        this.mIndicatorPaint = new Paint();
        this.mIndicatorPaint.setAntiAlias(true);
        this.mIndicatorPaint.setColor(indicatorColor);
        this.mIndicatorPaint.setStyle(Paint.Style.FILL);

        this.mBackgroundRectF = new RectF();
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setAntiAlias(true);
        this.mBackgroundPaint.setColor(backgroundColor);
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final int w = this.getWidth();
        final int h = this.getHeight();
        if (w == 0 || h == 0) {
            return;
        }
        final float indicatorRatio = this.mIndicatorRatio;
        final float indicatorRadius = this.mIndicatorRadius;
        RectF rf;

        // Draw background.
        rf = this.mBackgroundRectF;
        rf.set(0, 0, w, h);
        canvas.drawRoundRect(rf, indicatorRadius, indicatorRadius, this.mBackgroundPaint);

        float ratio = 0.f;
        final ScrollingView scrollingView = this.mScrollingView;
        if (scrollingView != null) {
            final int range = scrollingView.computeHorizontalScrollRange();
            final int extent = scrollingView.computeHorizontalScrollExtent();
            final int offset = scrollingView.computeHorizontalScrollOffset();
            final float r = Math.max(1.f, range - extent);
            ratio = (1.f - indicatorRatio) * (offset / r);
        }
        final int left = Math.round(w * ratio);
        final int indicatorWidth = Math.round(w * indicatorRatio);

        // Draw indicator.
        rf = this.mIndicatorRectF;
        rf.set(left, 0, left + indicatorWidth, h);
        canvas.drawRoundRect(rf, indicatorRadius, indicatorRadius, this.mIndicatorPaint);
    }

    /**
     * @deprecated
     */
    @Override
    public void setBackground(@Nullable Drawable background) {
        super.setBackground(null);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        this.mBackgroundPaint.setColor(color);
        this.invalidate();
    }

    public void setBackgroundColorRes(@ColorRes int colorRes) {
        final Context c = this.getContext();
        this.setBackgroundColor(ContextCompat.getColor(c, colorRes));
    }

    public void setIndicatorColor(@ColorInt int color) {
        this.mIndicatorPaint.setColor(color);
        this.invalidate();
    }

    public void setIndicatorColorRes(@ColorRes int colorRes) {
        final Context c = this.getContext();
        this.setIndicatorColor(ContextCompat.getColor(c, colorRes));
    }

    @FloatRange(from = 0.f, to = 1.f)
    public float getIndicatorRatio() {
        return this.mIndicatorRatio;
    }

    public void setIndicatorRatio(
            @FloatRange(from = 0.f, to = 1.f) float indicatorRatio) {
        if (this.mIndicatorRatio != indicatorRatio) {
            this.mIndicatorRatio = indicatorRatio;
            this.invalidate();
        }
    }

    public float getIndicatorRadius() {
        return this.mIndicatorRadius;
    }

    public void setIndicatorRadius(@Px float indicatorRadius) {
        if (this.mIndicatorRadius != indicatorRadius) {
            this.mIndicatorRadius = indicatorRadius;
            this.invalidate();
        }
    }

    @Nullable
    public <T extends ScrollingView> T getScrollingView() {
        return (T) this.mScrollingView;
    }

    public void setRecyclerView(@Nullable RecyclerView recyclerView) {
        final ScrollingView oldScrollingView = this.mScrollingView;
        if (oldScrollingView == recyclerView) {
            return;
        }
        if (this.mComponentListener != null) {
            RecyclerView oldRecyclerView = null;
            if (oldScrollingView instanceof RecyclerView) {
                oldRecyclerView = (RecyclerView) oldScrollingView;
            }
            if (oldRecyclerView != null) {
                oldRecyclerView.removeOnScrollListener(this.mComponentListener);
            }
        }
        this.mScrollingView = recyclerView;
        if (recyclerView != null) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            recyclerView.addOnScrollListener(this.mComponentListener);
        }
        this.postInvalidate();
    }

    public void setSliverContainer(@Nullable SliverContainer sliverContainer) {
        final ScrollingView oldScrollingView = this.mScrollingView;
        if (oldScrollingView == sliverContainer) {
            return;
        }
        if (this.mComponentListener2 != null) {
            SliverContainer oldSliverContainer = null;
            if (oldScrollingView instanceof SliverContainer) {
                oldSliverContainer = (SliverContainer) oldScrollingView;
            }
            if (oldSliverContainer != null) {
                oldSliverContainer.removeOnScrollListener(this.mComponentListener2);
            }
        }
        this.mScrollingView = sliverContainer;
        if (sliverContainer != null) {
            if (this.mComponentListener2 == null) {
                this.mComponentListener2 = new ComponentListener2();
            }
            sliverContainer.addOnScrollListener(this.mComponentListener2);
        }
        this.postInvalidate();
    }

    private final class ComponentListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            ScrollingIndicatorView.this.postInvalidate();
        }
    }

    private final class ComponentListener2 extends SliverContainer.SimpleOnScrollListener {
        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            ScrollingIndicatorView.this.postInvalidate();
        }
    }
}
