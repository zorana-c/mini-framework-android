package com.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2022/5/5
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ShadowFrameLayout extends FrameLayout {
    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;
    private static final int DEFAULT_SHADOW_COLOR = Color.parseColor("#29000000");

    private final Paint mShadowPaint;
    private final Paint mBackgroundPaint;
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    // 各个圆角的大小
    private int mShadowPaddingTop;
    private int mShadowPaddingLeft;
    private int mShadowPaddingRight;
    private int mShadowPaddingBottom;
    private boolean mMeasureAllChildren;

    // 各个圆角的显示
    private boolean mShadowTopEnabled;
    private boolean mShadowLeftEnabled;
    private boolean mShadowRightEnabled;
    private boolean mShadowBottomEnabled;

    // 各个圆角的属性
    private int mCornerRadius_topLeft;
    private int mCornerRadius_topRight;
    private int mCornerRadius_bottomLeft;
    private int mCornerRadius_bottomRight;

    private int mShadowColor;
    private int mShadowLimitSize;
    private int mBackgroundColor;
    private float mShadowOffsetX;
    private float mShadowOffsetY;
    private boolean isSymmetry;

    public ShadowFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ShadowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        this.initAttributes(context, attrs);
        this.mShadowPaint = new Paint();
        this.mShadowPaint.setAntiAlias(true);
        this.mShadowPaint.setStyle(Paint.Style.FILL);

        this.mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBackgroundPaint.setColor(this.mBackgroundColor);
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    private void initAttributes(@NonNull Context context, @Nullable AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowFrameLayout);
        try {
            this.mBackgroundColor = typedArray.getColor(R.styleable.ShadowFrameLayout_shadowBackground, Color.WHITE);
            this.mMeasureAllChildren = typedArray.getBoolean(R.styleable.ShadowFrameLayout_measureAllChildren, false);

            this.mShadowColor = typedArray.getColor(R.styleable.ShadowFrameLayout_shadowColor, DEFAULT_SHADOW_COLOR);
            this.mShadowOffsetX = typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowOffsetX, 0);
            this.mShadowOffsetY = typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowOffsetY, 0);
            this.mShadowLimitSize = typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowLimitSize, 0);
            this.isSymmetry = typedArray.getBoolean(R.styleable.ShadowFrameLayout_shadowSymmetry, true);
            this.mShadowTopEnabled = typedArray.getBoolean(R.styleable.ShadowFrameLayout_shadowTopEnabled, true);
            this.mShadowLeftEnabled = typedArray.getBoolean(R.styleable.ShadowFrameLayout_shadowLeftEnabled, true);
            this.mShadowRightEnabled = typedArray.getBoolean(R.styleable.ShadowFrameLayout_shadowRightEnabled, true);
            this.mShadowBottomEnabled = typedArray.getBoolean(R.styleable.ShadowFrameLayout_shadowBottomEnabled, true);

            final int cornerRadius = typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowCornerRadius, 0);
            this.setCornerRadius(cornerRadius);

            this.mCornerRadius_topLeft =
                    typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowCornerRadius_topLeft, this.mCornerRadius_topLeft);
            this.mCornerRadius_topRight =
                    typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowCornerRadius_topRight, this.mCornerRadius_topRight);
            this.mCornerRadius_bottomLeft =
                    typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowCornerRadius_bottomLeft, this.mCornerRadius_bottomLeft);
            this.mCornerRadius_bottomRight =
                    typedArray.getDimensionPixelSize(R.styleable.ShadowFrameLayout_shadowCornerRadius_bottomRight, this.mCornerRadius_bottomRight);
            this.mShadowColor = convertToAlphaColorInt(this.mShadowColor);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean measureMatchParentChildren = false;
        measureMatchParentChildren |= MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY;
        measureMatchParentChildren |= MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        int N = this.getChildCount();

        int decoratedMeasuredState = 0;
        int decoratedMeasuredWidth = 0;
        int decoratedMeasuredHeight = 0;
        this.mMatchParentChildren.clear();

        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child.getVisibility() != GONE || this.mMeasureAllChildren) {
                this.measureChildWithMargins(child,
                        widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                final int childDecoratedMeasuredWidth = this.getDecoratedMeasuredWidth(child);
                final int childDecoratedMeasuredHeight = this.getDecoratedMeasuredHeight(child);
                decoratedMeasuredWidth = Math.max(decoratedMeasuredWidth, childDecoratedMeasuredWidth);
                decoratedMeasuredHeight = Math.max(decoratedMeasuredHeight, childDecoratedMeasuredHeight);
                decoratedMeasuredState = ViewGroup.combineMeasuredStates(decoratedMeasuredState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (LayoutParams.MATCH_PARENT == layoutParams.width ||
                            LayoutParams.MATCH_PARENT == layoutParams.height) {
                        this.mMatchParentChildren.add(child);
                    }
                }
            }
        }

        // Account for padding too
        decoratedMeasuredWidth += this.getPaddingLeftWithShadow() + this.getPaddingRightWithShadow();
        decoratedMeasuredHeight += this.getPaddingTopWithShadow() + this.getPaddingBottomWithShadow();

        // Check against our minimum height and width
        decoratedMeasuredWidth = Math.max(decoratedMeasuredWidth, this.getSuggestedMinimumWidth());
        decoratedMeasuredHeight = Math.max(decoratedMeasuredHeight, this.getSuggestedMinimumHeight());

        // Check against our foreground's minimum height and width
        final Drawable foregroundDrawable = this.getForeground();
        if (foregroundDrawable != null) {
            decoratedMeasuredWidth = Math.max(decoratedMeasuredWidth, foregroundDrawable.getMinimumWidth());
            decoratedMeasuredHeight = Math.max(decoratedMeasuredHeight, foregroundDrawable.getMinimumHeight());
        }

        this.setMeasuredDimension(
                ViewGroup.resolveSizeAndState(decoratedMeasuredWidth, widthMeasureSpec, decoratedMeasuredState),
                ViewGroup.resolveSizeAndState(decoratedMeasuredHeight, heightMeasureSpec,
                        decoratedMeasuredState << MEASURED_HEIGHT_STATE_SHIFT));

        N = this.mMatchParentChildren.size();

        for (int index = 0; index < N; index++) {
            final View child = this.mMatchParentChildren.get(index);
            final int childWidthMeasureSpec;
            final int childHeightMeasureSpec;
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            if (LayoutParams.MATCH_PARENT == layoutParams.width) {
                final int width = Math.max(0, this.getMeasuredWidth()
                        - this.getPaddingLeftWithShadow()
                        - this.getPaddingRightWithShadow()
                        - layoutParams.leftMargin - layoutParams.rightMargin);
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        this.getPaddingLeftWithShadow()
                                + this.getPaddingRightWithShadow() +
                                layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width);
            }

            if (LayoutParams.MATCH_PARENT == layoutParams.height) {
                final int height = Math.max(0, this.getMeasuredHeight()
                        - this.getPaddingTopWithShadow()
                        - this.getPaddingBottomWithShadow()
                        - layoutParams.topMargin - layoutParams.bottomMargin);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            } else {
                childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        this.getPaddingTopWithShadow()
                                + this.getPaddingBottomWithShadow()
                                + layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        this.mMatchParentChildren.clear();
    }

    @Override
    protected void measureChild(@NonNull View child,
                                int parentWidthMeasureSpec,
                                int parentHeightMeasureSpec) {
        final ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                this.getPaddingLeftWithShadow()
                        + this.getPaddingRightWithShadow(), layoutParams.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                this.getPaddingTopWithShadow()
                        + this.getPaddingBottomWithShadow(), layoutParams.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(@NonNull View child,
                                           int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                this.getPaddingLeftWithShadow()
                        + this.getPaddingRightWithShadow()
                        + layoutParams.leftMargin
                        + layoutParams.rightMargin
                        + widthUsed, layoutParams.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                this.getPaddingTopWithShadow()
                        + this.getPaddingBottomWithShadow()
                        + layoutParams.topMargin
                        + layoutParams.bottomMargin
                        + heightUsed, layoutParams.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int N = this.getChildCount();

        final int parentLeft = this.getPaddingLeftWithShadow();
        final int parentRight = right - left - this.getPaddingRightWithShadow();

        final int parentTop = this.getPaddingTopWithShadow();
        final int parentBottom = bottom - top - this.getPaddingBottomWithShadow();

        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (View.GONE == child.getVisibility()) {
                continue;
            }
            this.layoutChild(child, parentLeft, parentTop, parentRight, parentBottom);
        }
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void layoutChild(@NonNull View child, int left, int top, int right, int bottom) {
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();
        int childTop;
        int childLeft;

        int gravity = layoutParams.gravity;
        if (gravity == -1) {
            gravity = DEFAULT_CHILD_GRAVITY;
        }

        final int layoutDirection = this.getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = left + (right - left - width) / 2 +
                        layoutParams.leftMargin - layoutParams.rightMargin;
                break;
            case Gravity.RIGHT:
                childLeft = right - width - layoutParams.rightMargin;
                break;
            default:
                childLeft = left + layoutParams.leftMargin;
        }

        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = top + (bottom - top - height) / 2 +
                        layoutParams.topMargin - layoutParams.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = bottom - height - layoutParams.bottomMargin;
                break;
            default:
                childTop = top + layoutParams.topMargin;
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);
    }

    public final int getPaddingTopWithShadow() {
        return this.getPaddingTop() + this.mShadowPaddingTop;
    }

    public final int getPaddingLeftWithShadow() {
        return this.getPaddingLeft() + this.mShadowPaddingLeft;
    }

    public final int getPaddingRightWithShadow() {
        return this.getPaddingRight() + this.mShadowPaddingRight;
    }

    public final int getPaddingBottomWithShadow() {
        return this.getPaddingBottom() + this.mShadowPaddingBottom;
    }

    public int getDecoratedMeasuredWidth(@NonNull View child) {
        final MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
    }

    public int getDecoratedMeasuredHeight(@NonNull View child) {
        final MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    public void setCornerRadius(@Px int radius) {
        this.mCornerRadius_topLeft = radius;
        this.mCornerRadius_topRight = radius;
        this.mCornerRadius_bottomLeft = radius;
        this.mCornerRadius_bottomRight = radius;
    }

    public void setCornerRadiusTopLeft(int radius) {
        this.mCornerRadius_topLeft = radius;
    }

    public void setCornerRadiusTopRight(int radius) {
        this.mCornerRadius_topRight = radius;
    }

    public void setCornerRadiusBottomLeft(int radius) {
        this.mCornerRadius_bottomLeft = radius;
    }

    public void setCornerRadiusBottomRight(int radius) {
        this.mCornerRadius_bottomRight = radius;
    }

    public void setShadowTopEnabled(boolean enabled) {
        this.mShadowTopEnabled = enabled;
    }

    public void setShadowLeftEnabled(boolean enabled) {
        this.mShadowLeftEnabled = enabled;
    }

    public void setShadowRightEnabled(boolean enabled) {
        this.mShadowRightEnabled = enabled;
    }

    public void setShadowBottomEnabled(boolean enabled) {
        this.mShadowBottomEnabled = enabled;
    }

    public void setShadowSymmetry(boolean symmetry) {
        this.isSymmetry = symmetry;
    }

    public void setShadowColor(@ColorInt int color) {
        this.mShadowColor = convertToAlphaColorInt(color);
    }

    public void setShadowColorRes(@ColorRes int resId) {
        this.setShadowColor(this.getResources().getColor(resId));
    }

    public void setShadowOffsetX(float shadowOffsetX) {
        this.mShadowOffsetX = shadowOffsetX;
    }

    public void setShadowOffsetY(float shadowOffsetY) {
        this.mShadowOffsetY = shadowOffsetY;
    }

    public void setShadowLimitSize(int shadowLimitSize) {
        this.mShadowLimitSize = shadowLimitSize;
    }

    public void setShadowBackgroundColor(@ColorInt int color) {
        this.mBackgroundColor = color;
        this.mBackgroundPaint.setColor(color);
    }

    public void setShadowBackgroundColorRes(@ColorRes int resId) {
        this.setShadowBackgroundColor(this.getResources().getColor(resId));
    }

    @Override
    public void requestLayout() {
        this.computeShadowPadding();
        super.requestLayout();
    }

    private void computeShadowPadding() {
        if (this.mShadowLimitSize <= 0) {
            this.mShadowPaddingTop = 0;
            this.mShadowPaddingLeft = 0;
            this.mShadowPaddingRight = 0;
            this.mShadowPaddingBottom = 0;
            return;
        }
        if (this.isSymmetry) {
            int paddingX = (int) (this.mShadowLimitSize + Math.abs(this.mShadowOffsetX));
            int paddingY = (int) (this.mShadowLimitSize + Math.abs(this.mShadowOffsetY));
            this.mShadowPaddingTop = this.mShadowTopEnabled ? paddingY : 0;
            this.mShadowPaddingLeft = this.mShadowLeftEnabled ? paddingX : 0;
            this.mShadowPaddingRight = this.mShadowRightEnabled ? paddingX : 0;
            this.mShadowPaddingBottom = this.mShadowBottomEnabled ? paddingY : 0;
        } else {
            if (Math.abs(this.mShadowOffsetX) > this.mShadowLimitSize) {
                if (this.mShadowOffsetX > 0) {
                    this.mShadowOffsetX = this.mShadowLimitSize;
                } else {
                    this.mShadowOffsetX = -this.mShadowLimitSize;
                }
            }
            if (Math.abs(this.mShadowOffsetY) > this.mShadowLimitSize) {
                if (this.mShadowOffsetY > 0) {
                    this.mShadowOffsetY = this.mShadowLimitSize;
                } else {
                    this.mShadowOffsetY = -this.mShadowLimitSize;
                }
            }
            this.mShadowPaddingTop = this.mShadowTopEnabled
                    ? (int) (this.mShadowLimitSize - this.mShadowOffsetY) : 0;
            this.mShadowPaddingLeft = this.mShadowLeftEnabled
                    ? (int) (this.mShadowLimitSize + this.mShadowOffsetX) : 0;
            this.mShadowPaddingRight = this.mShadowRightEnabled
                    ? (int) (this.mShadowLimitSize - this.mShadowOffsetX) : 0;
            this.mShadowPaddingBottom = this.mShadowBottomEnabled
                    ? (int) (this.mShadowLimitSize + this.mShadowOffsetY) : 0;
        }
    }

    protected float computeMaxCornerRadius() {
        float maxCornerRadius = 0f;
        maxCornerRadius = Math.max(maxCornerRadius, this.mCornerRadius_topLeft);
        maxCornerRadius = Math.max(maxCornerRadius, this.mCornerRadius_topRight);
        maxCornerRadius = Math.max(maxCornerRadius, this.mCornerRadius_bottomLeft);
        maxCornerRadius = Math.max(maxCornerRadius, this.mCornerRadius_bottomRight);
        return maxCornerRadius;
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int width = this.getRight() - this.getLeft();
            final int height = this.getBottom() - this.getTop();
            final RectF outRectF = new RectF();
            outRectF.top = this.mShadowPaddingTop;
            outRectF.left = this.mShadowPaddingLeft;
            outRectF.right = width - this.mShadowPaddingRight;
            outRectF.bottom = height - this.mShadowPaddingBottom;

            final float[] outerR = this.getCornerValue((int) outRectF.height());
            final Path outPath = new Path();
            outPath.addRoundRect(outRectF, outerR, Path.Direction.CW);
            canvas.clipPath(outPath);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    @CallSuper
    protected void onDraw(@NonNull Canvas canvas) {
        this.dispatchDrawShadow(canvas);
        super.onDraw(canvas);
    }

    protected void dispatchDrawShadow(@NonNull Canvas canvas) {
        final int width = this.getRight() - this.getLeft();
        final int height = this.getBottom() - this.getTop();
        final float cornerRadius = this.computeMaxCornerRadius();

        final Bitmap bitmap = this.createShadowBitmap(width, height,
                this.mShadowLimitSize,
                this.mShadowOffsetX,
                this.mShadowOffsetY, this.mShadowColor, Color.TRANSPARENT);
        final BitmapDrawable bitmapDrawable = new BitmapDrawable(this.getResources(), bitmap);
        bitmapDrawable.setBounds(0, 0, width, height);
        bitmapDrawable.draw(canvas);

        final RectF outRectF = new RectF();
        outRectF.top = this.mShadowPaddingTop;
        outRectF.left = this.mShadowPaddingLeft;
        outRectF.right = width - this.mShadowPaddingRight;
        outRectF.bottom = height - this.mShadowPaddingBottom;

        final float targetRadius = outRectF.height() / 2.f;
        if (cornerRadius > targetRadius) {
            canvas.drawRoundRect(outRectF, targetRadius, targetRadius, this.mBackgroundPaint);
        } else {
            this.drawSpaceCorner(canvas, outRectF);
        }
    }

    protected void drawSpaceCorner(@NonNull Canvas canvas, @NonNull RectF outRectF) {
        final float[] outerR = this.getCornerValue((int) outRectF.height());
        final RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        final ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        final Paint paint = this.mBackgroundPaint;
        if (paint.getShader() == null) {
            shapeDrawable.getPaint().setColor(paint.getColor());
        } else {
            shapeDrawable.getPaint().setShader(paint.getShader());
        }
        shapeDrawable.setBounds(
                (int) outRectF.left, (int) outRectF.top,
                (int) outRectF.right, (int) outRectF.bottom);
        shapeDrawable.draw(canvas);
    }

    @NonNull
    protected Bitmap createShadowBitmap(int width, int height,
                                        float shadowLimitSize,
                                        float shadowOffsetX,
                                        float shadowOffsetY,
                                        @ColorInt int shadowColorInt,
                                        @ColorInt int shadowFillColorInt) {
        width = Math.max(1, width / 4);
        height = Math.max(1, height / 4);
        shadowOffsetX = shadowOffsetX / 4;
        shadowOffsetY = shadowOffsetY / 4;
        shadowLimitSize = shadowLimitSize / 4;

        final Paint shadowPaint = this.mShadowPaint;
        shadowPaint.setColor(shadowFillColorInt);
        shadowPaint.setShadowLayer(shadowLimitSize / 2.f, shadowOffsetX, shadowOffsetY, shadowColorInt);

        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final RectF outRectF = new RectF();
        outRectF.top = this.mShadowTopEnabled ? shadowLimitSize : 0;
        outRectF.left = this.mShadowLeftEnabled ? shadowLimitSize : 0;
        outRectF.right = this.mShadowRightEnabled ? width - shadowLimitSize : 0;
        outRectF.bottom = this.mShadowBottomEnabled ? height - shadowLimitSize : 0;

        if (this.isSymmetry) {
            if (shadowOffsetX > 0) {
                outRectF.left += shadowOffsetX;
                outRectF.right -= shadowOffsetX;
            } else if (shadowOffsetX < 0) {
                outRectF.left += Math.abs(shadowOffsetX);
                outRectF.right -= Math.abs(shadowOffsetX);
            }

            if (shadowOffsetY > 0) {
                outRectF.top += shadowOffsetY;
                outRectF.bottom -= shadowOffsetY;
            } else if (shadowOffsetY < 0) {
                outRectF.top += Math.abs(shadowOffsetY);
                outRectF.bottom -= Math.abs(shadowOffsetY);
            }
        } else {
            outRectF.top -= shadowOffsetY;
            outRectF.left -= shadowOffsetX;
            outRectF.right -= shadowOffsetX;
            outRectF.bottom -= shadowOffsetY;
        }
        final float leftTop = this.mCornerRadius_topLeft / 4.f;
        final float leftBottom = this.mCornerRadius_bottomLeft / 4.f;
        final float rightTop = this.mCornerRadius_topRight / 4.f;
        final float rightBottom = this.mCornerRadius_bottomRight / 4.f;
        final float[] outerRadii = new float[]{
                leftTop, leftTop, rightTop, rightTop,
                rightBottom, rightBottom, leftBottom, leftBottom};

        final Path outPath = new Path();
        outPath.addRoundRect(outRectF, outerRadii, Path.Direction.CW);
        canvas.drawPath(outPath, shadowPaint);
        return output;
    }

    @NonNull
    protected float[] getCornerValue(int trueHeight) {
        int topLeft = this.mCornerRadius_topLeft;
        int topRight = this.mCornerRadius_topRight;
        int bottomLeft = this.mCornerRadius_bottomLeft;
        int bottomRight = this.mCornerRadius_bottomRight;
        if (topLeft > trueHeight / 2) {
            topLeft = trueHeight / 2;
        }
        if (topRight > trueHeight / 2) {
            topRight = trueHeight / 2;
        }
        if (bottomLeft > trueHeight / 2) {
            bottomLeft = trueHeight / 2;
        }
        if (bottomRight > trueHeight / 2) {
            bottomRight = trueHeight / 2;
        }
        return new float[]{topLeft, topLeft, topRight, topRight,
                bottomRight, bottomRight, bottomLeft, bottomLeft};
    }

    private static int convertToAlphaColorInt(@ColorInt int color) {
        // 获取单签颜色值的透明度，如果没有设置透明度，默认加上#2a
        if (Color.alpha(color) == 255) {
            String red = Integer.toHexString(Color.red(color));
            String green = Integer.toHexString(Color.green(color));
            String blue = Integer.toHexString(Color.blue(color));
            if (red.length() == 1) {
                red = "0" + red;
            }
            if (green.length() == 1) {
                green = "0" + green;
            }
            if (blue.length() == 1) {
                blue = "0" + blue;
            }
            return Color.parseColor("#2a" + red + green + blue);
        }
        return color;
    }
}
