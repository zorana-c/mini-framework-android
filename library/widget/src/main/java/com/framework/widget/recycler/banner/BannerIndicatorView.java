package com.framework.widget.recycler.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.R;
import com.framework.widget.compat.UIViewCompat;
import com.framework.widget.recycler.pager.PagerLayoutManager;

/**
 * @Author create by Zhengzelong on 2023-03-28
 * @Email : 171905184@qq.com
 * @Description :
 */
public class BannerIndicatorView extends View {
    private final RectF mIndicatorRectF;
    private final Paint mIndicatorPaint;
    private final Paint mUnIndicatorPaint;

    private float mIndicatorRadius;
    private float mIndicatorInterval;

    private float mSelectedIndicatorWidth;

    private float mIndicatorWidth;
    private float mIndicatorHeight;

    private int mIndicatorCount = 0;
    private int mCurrentPosition = 0;

    public BannerIndicatorView(@NonNull Context context) {
        this(context, null);
    }

    public BannerIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIndicatorRectF = new RectF();

        this.mIndicatorPaint = new Paint();
        this.mIndicatorPaint.setAntiAlias(true);
        this.mIndicatorPaint.setColor(Color.WHITE);
        this.mIndicatorPaint.setStyle(Paint.Style.FILL);

        this.mUnIndicatorPaint = new Paint();
        this.mUnIndicatorPaint.setAntiAlias(true);
        this.mUnIndicatorPaint.setStyle(Paint.Style.FILL);
        this.mUnIndicatorPaint.setColor(Color.parseColor("#8AFFFFFF"));

        this.mIndicatorRadius = UIViewCompat.dip2px(context, 4.f);
        this.mIndicatorInterval = UIViewCompat.dip2px(context, 4.f);

        this.mIndicatorWidth = UIViewCompat.dip2px(context, 8.f);
        this.mIndicatorHeight = UIViewCompat.dip2px(context, 4.f);
        this.mSelectedIndicatorWidth = UIViewCompat.dip2px(context, 8.f);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicatorView);
        this.mIndicatorWidth = typedArray.getDimension(R.styleable.BannerIndicatorView_indicatorWidth, this.mIndicatorWidth);
        this.mIndicatorHeight = typedArray.getDimension(R.styleable.BannerIndicatorView_indicatorHeight, this.mIndicatorHeight);
        this.mIndicatorRadius = typedArray.getDimension(R.styleable.BannerIndicatorView_indicatorRadius, this.mIndicatorRadius);
        this.mIndicatorInterval = typedArray.getDimension(R.styleable.BannerIndicatorView_indicatorInterval, this.mIndicatorInterval);
        this.mSelectedIndicatorWidth = typedArray.getDimension(R.styleable.BannerIndicatorView_indicatorSelectedWidth, this.mSelectedIndicatorWidth);
        this.mIndicatorPaint.setColor(typedArray.getColor(R.styleable.BannerIndicatorView_indicatorSelectedColor, Color.WHITE));
        this.mUnIndicatorPaint.setColor(typedArray.getColor(R.styleable.BannerIndicatorView_indicatorUnSelectedColor, Color.parseColor("#8AFFFFFF")));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minWidth = this.getSuggestedMinimumWidth();
        final int minHeight = this.getSuggestedMinimumHeight();
        final int width = this.getWidthMeasureSize(minWidth, widthMeasureSpec);
        final int height = this.getHeightMeasureSize(minHeight, heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    private int getWidthMeasureSize(int size, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        result += this.getPaddingLeft();
        result += this.getPaddingRight();
        switch (specMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                final int indicatorCount = this.mIndicatorCount;
                if (indicatorCount > 0) {
                    result += Math.round(this.mSelectedIndicatorWidth)
                            + Math.round(this.mIndicatorWidth) * (indicatorCount - 1)
                            + Math.round(this.mIndicatorInterval) * (indicatorCount - 1);
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return Math.max(size, result);
    }

    private int getHeightMeasureSize(int size, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        result += this.getPaddingLeft();
        result += this.getPaddingRight();
        switch (specMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result += Math.round(this.mIndicatorHeight);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return Math.max(size, result);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final int width = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        final int height = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();

        final RectF indicatorRectF = this.mIndicatorRectF;
        final float indicatorRadius = this.mIndicatorRadius;
        final float indicatorWidth = this.mIndicatorWidth;
        final float indicatorHeight = this.mIndicatorHeight;

        float layoutTop = (height - indicatorHeight) / 2.f + this.getPaddingTop();
        float layoutLeft = this.getPaddingLeft();

        for (int position = 0; position < this.mIndicatorCount; position++) {
            indicatorRectF.set(layoutLeft, layoutTop,
                    layoutLeft + indicatorWidth,
                    layoutTop + indicatorHeight);

            if (position == this.mCurrentPosition) {
                indicatorRectF.set(layoutLeft, layoutTop,
                        layoutLeft + this.mSelectedIndicatorWidth,
                        layoutTop + indicatorHeight);

                canvas.drawRoundRect(
                        indicatorRectF,
                        indicatorRadius,
                        indicatorRadius,
                        this.mIndicatorPaint);

                layoutLeft += this.mSelectedIndicatorWidth;
            } else {
                canvas.drawRoundRect(
                        indicatorRectF,
                        indicatorRadius,
                        indicatorRadius,
                        this.mUnIndicatorPaint);

                layoutLeft += indicatorWidth;
            }
            layoutLeft += this.mIndicatorInterval;
        }
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private PagerLayoutManager.OnPageChangeListener mOnPageChangeListener;

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        final RecyclerView oldRecyclerView = this.mRecyclerView;
        if (oldRecyclerView == recyclerView) {
            return;
        }
        if (oldRecyclerView != null) {
            final PagerLayoutManager lm;
            lm = (PagerLayoutManager) oldRecyclerView.getLayoutManager();
            if (lm != null) {
                lm.removeOnPageChangeListener(this.mOnPageChangeListener);
            }
            final RecyclerView.Adapter<?> ad = oldRecyclerView.getAdapter();
            if (ad != null) {
                ad.unregisterAdapterDataObserver(this.mAdapterDataObserver);
            }
        }
        this.mRecyclerView = recyclerView;
        if (recyclerView != null) {
            final PagerLayoutManager lm;
            lm = (PagerLayoutManager) recyclerView.getLayoutManager();
            if (lm != null) {
                if (this.mOnPageChangeListener == null) {
                    this.mOnPageChangeListener = new OnPageChangeListener();
                }
                lm.addOnPageChangeListener(this.mOnPageChangeListener);
            } else throw new IllegalStateException("ERROR");
            final RecyclerView.Adapter<?> ad = recyclerView.getAdapter();
            if (ad != null) {
                if (this.mAdapterDataObserver == null) {
                    this.mAdapterDataObserver = new AdapterDataObserver();
                }
                ad.registerAdapterDataObserver(this.mAdapterDataObserver);
                ad.notifyDataSetChanged();
            } else throw new IllegalStateException("ERROR");
        }
        this.dateSetChanged(0);
    }

    private void dateSetChanged() {
        int position = RecyclerView.NO_POSITION;
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            final PagerLayoutManager lm;
            lm = (PagerLayoutManager) recyclerView.getLayoutManager();
            position = lm.getCurrentPosition();
        }
        this.dateSetChanged(position);
    }

    private void dateSetChanged(int position) {
        int itemCount = 0;
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            final RecyclerView.Adapter<?> ad = recyclerView.getAdapter();
            if (ad != null) {
                itemCount = ad.getItemCount();
            }
        }
        position = Math.max(0, Math.min(position, itemCount - 1));
        if (this.mIndicatorCount != itemCount) {
            this.mIndicatorCount = itemCount;
            this.requestLayout();
        }
        if (this.mCurrentPosition != position) {
            this.mCurrentPosition = position;
            this.invalidate();
        }
    }

    final class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            BannerIndicatorView.this.dateSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            BannerIndicatorView.this.dateSetChanged();
        }
    }

    final class OnPageChangeListener implements PagerLayoutManager.OnPageChangeListener {
        @Override
        public void onPageScrolled(@NonNull RecyclerView recyclerView, int position,
                                   float positionOffset, int positionOffsetPixels) {
            BannerIndicatorView.this.dateSetChanged(position);
        }

        @Override
        public void onPageSelected(@NonNull RecyclerView recyclerView, int position) {
            BannerIndicatorView.this.dateSetChanged(position);
        }
    }
}
