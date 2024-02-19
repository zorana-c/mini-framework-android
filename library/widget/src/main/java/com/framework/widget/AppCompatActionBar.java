package com.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * @Author create by Zhengzelong on 2024-01-23
 * @Email : 171905184@qq.com
 * @Description :
 */
public class AppCompatActionBar extends FrameLayout {
    @Nullable
    private Drawable mStatusBarBackground;
    @Nullable
    private WindowInsets mStatusBarInsets;

    private boolean mStatusBarBackgroundEnabled;

    public AppCompatActionBar(@NonNull Context context) {
        this(context, null);
    }

    public AppCompatActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceType")
    public AppCompatActionBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppCompatActionBar);
        this.mStatusBarBackground = typedArray.getDrawable(R.styleable.AppCompatActionBar_statusBarBackground);
        typedArray.recycle();

        this.setWillNotDraw(true); // No need to draw until the insets are adjusted
        if (ViewCompat.getFitsSystemWindows(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.setOnApplyWindowInsetsListener((widget, insets) -> {
                    ((AppCompatActionBar) widget).setChildInsets(insets);
                    return insets.consumeSystemWindowInsets();
                });
                this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                if (this.mStatusBarBackground == null) {
                    final int[] THEME_ATTRS = {
                            android.R.attr.colorPrimaryDark
                    };
                    typedArray = context.obtainStyledAttributes(THEME_ATTRS);
                    this.mStatusBarBackground = typedArray.getDrawable(0);
                    typedArray.recycle();
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.applyChildrenWindowInsets();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void applyChildrenWindowInsets() {
        WindowInsets insets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            insets = this.mStatusBarInsets;
        }
        if (insets == null) {
            return;
        }
        final int N = this.getChildCount();
        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (!this.getMeasureAllChildren()
                    && child.getVisibility() == View.GONE) {
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                insets = insets.replaceSystemWindowInsets(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                if (ViewCompat.getFitsSystemWindows(child)) {
                    child.dispatchApplyWindowInsets(insets);
                } else {
                    final LayoutParams layoutParams;
                    layoutParams = (LayoutParams) child.getLayoutParams();
                    layoutParams.leftMargin = insets.getSystemWindowInsetLeft();
                    layoutParams.topMargin = insets.getSystemWindowInsetTop();
                    layoutParams.rightMargin = insets.getSystemWindowInsetRight();
                    layoutParams.bottomMargin = insets.getSystemWindowInsetBottom();
                }
            }
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (!this.mStatusBarBackgroundEnabled
                || this.mStatusBarBackground == null) {
            return;
        }
        int inset = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this.mStatusBarInsets != null) {
                inset = this.mStatusBarInsets.getSystemWindowInsetTop();
            }
        }
        if (inset > 0) {
            this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), inset);
            this.mStatusBarBackground.draw(canvas);
        }
    }

    public void setChildInsets(@NonNull WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final boolean draw;
            draw = insets.getSystemWindowInsetTop() > 0;
            if (draw) {
                this.setWillNotDraw(false);
            } else {
                this.setWillNotDraw(this.getBackground() == null);
            }
            this.mStatusBarInsets = insets;
            this.mStatusBarBackgroundEnabled = draw;
            this.requestLayout();
        }
    }

    @Nullable
    public Drawable getStatusBarBackground() {
        return this.mStatusBarBackground;
    }

    public void setStatusBarBackground(@Nullable Drawable background) {
        this.mStatusBarBackground = background;
        this.invalidate();
    }

    public void setStatusBarBackgroundResource(@DrawableRes int resId) {
        final Context context = this.getContext();
        final Drawable background;
        background = ContextCompat.getDrawable(context, resId);
        this.setStatusBarBackground(background);
    }

    public void setStatusBarBackgroundColor(@ColorInt int colorInt) {
        this.setStatusBarBackground(new ColorDrawable(colorInt));
    }

    public void setStatusBarBackgroundColorResource(@ColorRes int resId) {
        final Context context = this.getContext();
        final int colorInt;
        colorInt = ContextCompat.getColor(context, resId);
        this.setStatusBarBackgroundColor(colorInt);
    }
}
