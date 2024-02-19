package com.framework.widget.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author create by Zhengzelong on 2023-12-14
 * @Email : 171905184@qq.com
 * @Description :
 */
public class BackgroundColorSpan extends ReplacementSpan {
    @NonNull
    private final RectF mBackgroundRectF = new RectF();
    @NonNull
    private final Paint mBackgroundPaint = new Paint();

    private float mRadius;
    private float mMarginLeft;
    private float mMarginRight;
    private float mPaddingLeft;
    private float mPaddingRight;

    @ColorInt
    private int mTextColor;

    public BackgroundColorSpan(@ColorInt int color) {
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mBackgroundPaint.setColor(color);
        this.mBackgroundPaint.setAntiAlias(true);
    }

    @Override
    public int getSize(@NonNull Paint paint,
                       @Nullable CharSequence text, int start, int end,
                       @Nullable Paint.FontMetricsInt fm) {
        final float size = paint.measureText(text, start, end);
        return (int) (size
                + this.mMarginLeft + this.mMarginRight
                + this.mPaddingLeft + this.mPaddingRight);
    }

    @Override
    public void draw(@NonNull Canvas canvas,
                     @Nullable CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        final Paint.FontMetrics metrics = paint.getFontMetrics();
        final float width = paint.measureText(text, start, end)
                + this.mPaddingLeft + this.mPaddingRight;
        final float height = metrics.descent - metrics.ascent;
        float startX = x + this.mMarginLeft;
        float startY = y + metrics.ascent;

        final int saveCount = canvas.save();
        final RectF rectF = this.mBackgroundRectF;
        rectF.set(
                (int) startX,
                (int) startY,
                (int) (startX + width),
                (int) (startY + height));
        canvas.drawRoundRect(rectF, this.mRadius, this.mRadius, this.mBackgroundPaint);
        canvas.restoreToCount(saveCount);

        if (this.mTextColor != 0) {
            paint.setColor(this.mTextColor);
        }
        canvas.drawText(text, start, end, startX + this.mPaddingLeft, y, paint);
    }

    @NonNull
    public BackgroundColorSpan setTextColor(@ColorInt int color) {
        this.mTextColor = color;
        return this;
    }

    @NonNull
    public BackgroundColorSpan setMarginLeft(float marginLeft) {
        this.mMarginLeft = marginLeft;
        return this;
    }

    @NonNull
    public BackgroundColorSpan setMarginRight(float marginRight) {
        this.mMarginRight = marginRight;
        return this;
    }

    @NonNull
    public BackgroundColorSpan setPaddingLeft(float paddingLeft) {
        this.mPaddingLeft = paddingLeft;
        return this;
    }

    @NonNull
    public BackgroundColorSpan setPaddingRight(float paddingRight) {
        this.mPaddingRight = paddingRight;
        return this;
    }

    @NonNull
    public BackgroundColorSpan setBackgroundRadius(float radius) {
        this.mRadius = radius;
        return this;
    }
}
