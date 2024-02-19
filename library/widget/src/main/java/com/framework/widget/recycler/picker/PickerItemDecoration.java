package com.framework.widget.recycler.picker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PickerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String COLOR = "#fff2f2f2";
    private final Rect mBackgroundRect = new Rect();
    private final Paint mForegroundPaint = new Paint();
    // 最后绘制信息
    private float mLastHeadDecoration;
    private float mLastTailDecoration;
    @Nullable
    private Drawable mBackgroundDrawable;

    public PickerItemDecoration(@NonNull Context context) {
        this.mForegroundPaint.setStrokeWidth(2.0f);
        this.mForegroundPaint.setStyle(Paint.Style.STROKE);
        this.mForegroundPaint.setColor(Color.parseColor(COLOR));
    }

    /**
     * 绘制背景色(长条线条)
     */
    @Override
    public void onDraw(@NonNull Canvas canvas,
                       @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        final Drawable background = this.mBackgroundDrawable;
        if (background == null) {
            return;
        }
        final PickerLayoutManager lm;
        lm = (PickerLayoutManager) parent.getLayoutManager();
        if (lm == null || lm.getChildCount() == 0) {
            return;
        }
        int position = lm.getCurrentPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = 0;
        }
        float headDecoration = this.mLastHeadDecoration;
        float tailDecoration = this.mLastTailDecoration;
        final View itemView = lm.findViewByPosition(position);
        if (itemView != null) {
            final float centerItemView = itemView.getHeight() / 2.f;
            final float centerContainer = (lm.getHeight()
                    - lm.getPaddingTop() - lm.getPaddingBottom()) / 2.f;
            headDecoration = centerContainer - centerItemView;
            tailDecoration = centerContainer + centerItemView;
        }
        final float left = 0;
        final float right = left + lm.getWidth();
        final Rect bounds = this.mBackgroundRect;
        bounds.top = (int) headDecoration;
        bounds.left = (int) left;
        bounds.right = (int) right;
        bounds.bottom = (int) tailDecoration;
        canvas.save();
        background.setBounds(bounds);
        background.draw(canvas);
        canvas.restore();
        this.mLastHeadDecoration = headDecoration;
        this.mLastTailDecoration = tailDecoration;
    }

    /**
     * 绘制前景色(两条线条)
     */
    @Override
    public void onDrawOver(@NonNull Canvas canvas,
                           @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        final Paint paint = this.mForegroundPaint;
        if (paint.getStrokeWidth() == 0) {
            return;
        }
        final PickerLayoutManager lm;
        lm = (PickerLayoutManager) parent.getLayoutManager();
        if (lm == null || lm.getChildCount() == 0) {
            return;
        }
        int position = lm.getCurrentPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = 0;
        }
        float headDecoration = this.mLastHeadDecoration;
        float tailDecoration = this.mLastTailDecoration;
        final View itemView = lm.findViewByPosition(position);
        if (itemView != null) {
            final float centerItemView = itemView.getHeight() / 2.f;
            final float centerContainer = (lm.getHeight()
                    - lm.getPaddingTop() - lm.getPaddingBottom()) / 2.f;
            headDecoration = centerContainer - centerItemView;
            tailDecoration = centerContainer + centerItemView;
        }
        final float left = 0;
        final float right = left + lm.getWidth();
        canvas.save();
        canvas.drawLine(left, headDecoration, right, headDecoration, paint);
        canvas.drawLine(left, tailDecoration, right, tailDecoration, paint);
        canvas.restore();
        this.mLastHeadDecoration = headDecoration;
        this.mLastTailDecoration = tailDecoration;
    }

    @NonNull
    public PickerItemDecoration setLineWidth(int size) {
        this.mForegroundPaint.setStrokeWidth(size);
        return this;
    }

    @NonNull
    public PickerItemDecoration setLineColor(@ColorInt int color) {
        this.mForegroundPaint.setColor(color);
        return this;
    }

    @NonNull
    public PickerItemDecoration setBackgroundColor(@ColorInt int color) {
        return this.setBackground(new ColorDrawable(color));
    }

    @NonNull
    public PickerItemDecoration setBackground(@Nullable Drawable drawable) {
        this.mBackgroundDrawable = drawable;
        return this;
    }
}
