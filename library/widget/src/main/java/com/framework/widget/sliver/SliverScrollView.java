package com.framework.widget.sliver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.R;

/**
 * @Author create by Zhengzelong on 2023-02-28
 * @Email : 171905184@qq.com
 * @Description : 滑动控件
 * <p>
 * 1.支持视图背景拉伸功能
 */
public class SliverScrollView extends SliverContainer {
    @NonNull
    private final Matrix mMatrix = new Matrix();

    @Nullable
    private Matrix mDrawMatrix;
    @Nullable
    private Drawable mDrawable;

    private int mDrawableWidth;
    private int mDrawableHeight;

    public SliverScrollView(@NonNull Context context) {
        this(context, null);
    }

    public SliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        this.addOnScrollListener(new ComponentListener());

        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliverScrollView);
        final Drawable drawable;
        drawable = typedArray.getDrawable(R.styleable.SliverScrollView_sliverZoomSrc);
        typedArray.recycle();
        this.setZoomImageDrawable(drawable);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.configureBounds();
    }

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width != oldWidth || height != oldHeight) {
            this.configureBounds();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        final Drawable drawable = this.mDrawable;
        if (drawable == null) {
            return;
        }
        final View child = this.getChildClosestToHead();
        if (child == null
                || child.getVisibility() == View.GONE) {
            return;
        }
        final int childWidth = child.getWidth();
        final int childHeight = child.getHeight();
        final int layoutX = child.getLeft();
        final int layoutY = child.getTop();
        final int offsetX = Math.min(layoutX, 0);
        final int offsetY = Math.min(layoutY, 0);
        final float px = childWidth / 2.f;
        final float py = childHeight / 2.f;

        final float pctX = this.getPct(layoutX);
        final float pctY = this.getPct(layoutY);
        float pct = 0.f;
        if (this.canScrollHorizontally()) {
            pct = pctX;
        }
        if (this.canScrollVertically()) {
            pct = pctY;
        }
        pct += 1.f;

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.scale(pct, pct, px, py);
        canvas.translate(offsetX, offsetY);

        final Matrix matrix = this.mDrawMatrix;
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        if (drawable == this.mDrawable) {
            final int width = drawable.getIntrinsicWidth();
            final int height = drawable.getIntrinsicHeight();
            if (width != this.mDrawableWidth
                    || height != this.mDrawableHeight) {
                this.mDrawableWidth = width;
                this.mDrawableHeight = height;
                this.configureBounds();
            }
            this.invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    @Nullable
    public Drawable getZoomDrawable() {
        return this.mDrawable;
    }

    public void setZoomImageColor(@ColorInt int color) {
        this.setZoomImageDrawable(new ColorDrawable(color));
    }

    public void setZoomImageBitmap(@Nullable Bitmap bitmap) {
        this.setZoomImageDrawable(new BitmapDrawable(bitmap));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setZoomImageResource(@DrawableRes int resId) {
        final Resources resources = this.getResources();
        this.setZoomImageDrawable(resources.getDrawable(resId));
    }

    public void setZoomImageDrawable(@Nullable Drawable drawable) {
        final Drawable oldDrawable = this.mDrawable;
        if (oldDrawable == drawable) {
            return;
        }
        if (oldDrawable != null) {
            oldDrawable.setCallback(null);
            oldDrawable.setVisible(false, false);
            this.unscheduleDrawable(oldDrawable);
        }
        final int oldWidth = this.mDrawableWidth;
        final int oldHeight = this.mDrawableHeight;
        this.mDrawable = drawable;
        this.mDrawableWidth = -1;
        this.mDrawableHeight = -1;

        if (drawable != null) {
            drawable.setCallback(this);
            if (drawable.isStateful()) {
                drawable.setState(this.getDrawableState());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                drawable.setLayoutDirection(this.getLayoutDirection());
            }
            this.mDrawableWidth = drawable.getIntrinsicWidth();
            this.mDrawableHeight = drawable.getIntrinsicHeight();
            this.configureBounds();
        }
        if (oldWidth != this.mDrawableWidth
                || oldHeight != this.mDrawableHeight) {
            this.requestLayout();
        }
        this.invalidate();
    }

    private void configureBounds() {
        final Drawable drawable = this.mDrawable;
        if (drawable == null) {
            return;
        }
        final View child = this.getChildClosestToHead();
        if (child == null) {
            return;
        }
        final int dWidth = this.mDrawableWidth;
        final int dHeight = this.mDrawableHeight;
        final int vWidth = child.getMeasuredWidth();
        final int vHeight = child.getMeasuredHeight();

        if (dWidth <= 0 || dHeight <= 0) {
            drawable.setBounds(0, 0, vWidth, vHeight);
            this.mDrawMatrix = null;
        } else {
            drawable.setBounds(0, 0, dWidth, dHeight);
            float scale;
            float dx = 0;
            float dy = 0;

            if (dWidth * vHeight > vWidth * dHeight) {
                scale = (float) vHeight / (float) dHeight;
                dx = (vWidth - dWidth * scale) * 0.5f;
            } else {
                scale = (float) vWidth / (float) dWidth;
                dy = (vHeight - dHeight * scale) * 0.5f;
            }
            this.mDrawMatrix = this.mMatrix;
            this.mDrawMatrix.setScale(scale, scale);
            this.mDrawMatrix.postTranslate(Math.round(dx), Math.round(dy));
        }
    }

    protected float getPct(final int offset) {
        float pct = offset / 250.f;
        if (pct <= 0.f) {
            pct = 0.f;
        }
        return pct;
    }

    private void invalidateDraw() {
        if (this.mDrawable != null) {
            this.invalidate();
        }
    }

    private static final class ComponentListener extends SliverContainer.SimpleOnScrollListener {
        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            ((SliverScrollView) sliverContainer).invalidateDraw();
        }

        @Override
        public void onScrollStateChanged(@NonNull SliverContainer sliverContainer, int scrollState) {
            ((SliverScrollView) sliverContainer).invalidateDraw();
        }
    }
}
