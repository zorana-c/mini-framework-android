package com.framework.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

/**
 * @Author create by Zoran on 2020/8/2
 * @Email : 171905184@qq.com
 * @Description :
 */
public class AppCompatActionBar extends LinearLayoutCompat {
    @NonNull
    private final View mStatusBar;
    @Nullable
    private View mToolsBar;

    public AppCompatActionBar(@NonNull Context context) {
        this(context, null);
    }

    public AppCompatActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatActionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutParams layoutParams = this.generateDefaultLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;

        this.mStatusBar = new AppCompatStatusBar(context);
        this.mStatusBar.setLayoutParams(layoutParams);
        this.addView(this.mStatusBar);
        this.setOrientation(VERTICAL);
    }

    @Override
    public boolean onHoverEvent(@NonNull MotionEvent event) {
        super.onHoverEvent(event);
        return true;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public ActionMode startActionModeForChild(@NonNull View child,
                                              @NonNull ActionMode.Callback callback) {
        return null;
    }

    @Override
    public ActionMode startActionModeForChild(@NonNull View child,
                                              @NonNull ActionMode.Callback callback, int type) {
        if (type != ActionMode.TYPE_PRIMARY) {
            return super.startActionModeForChild(child, callback, type);
        }
        return null;
    }

    public void setBackgroundAlpha(@FloatRange(from = 0, to = 1.f) float alpha) {
        Drawable backgroundDrawable = this.getBackground();
        if (backgroundDrawable == null) {
            backgroundDrawable = new ColorDrawable(Color.WHITE);
        }
        backgroundDrawable.setAlpha((int) (255 * alpha));
        this.setBackground(backgroundDrawable);
    }

    // StatusBar

    public void setStatusBarEnabled(boolean enabled) {
        this.mStatusBar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setStatusBarId(@IdRes int id) {
        this.mStatusBar.setId(id);
    }

    public void setStatusBarBackground(@Nullable Drawable drawable) {
        this.mStatusBar.setBackground(drawable);
    }

    public void setStatusBarBackgroundColor(@ColorInt int color) {
        this.mStatusBar.setBackgroundColor(color);
    }

    public void setStatusBarBackgroundResource(@DrawableRes int resId) {
        this.mStatusBar.setBackgroundResource(resId);
    }

    public void setStatusBarBackgroundAlpha(@FloatRange(from = 0, to = 1.f) float alpha) {
        Drawable backgroundDrawable = this.mStatusBar.getBackground();
        if (backgroundDrawable == null) {
            backgroundDrawable = new ColorDrawable(Color.WHITE);
        }
        backgroundDrawable.setAlpha((int) (255 * alpha));
        this.mStatusBar.setBackground(backgroundDrawable);
    }

    public int getStatusBarWidth() {
        return this.mStatusBar.getMeasuredWidth();
    }

    public int getStatusBarHeight() {
        return this.mStatusBar.getMeasuredHeight();
    }

    @Nullable
    public Drawable getStatusBarBackground() {
        return this.mStatusBar.getBackground();
    }

    @NonNull
    public View getStatusBar() {
        return this.mStatusBar;
    }

    // ToolsBar

    public void setToolsBar(@LayoutRes int layoutId) {
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(this.getContext());
        this.setToolsBar(inflater.inflate(layoutId, this, false));
    }

    public void setToolsBar(@Nullable View child) {
        final View oldChild = this.mToolsBar;
        if (oldChild == child) {
            return;
        }
        if (oldChild != null) {
            this.removeView(oldChild);
        }
        this.mToolsBar = child;
        if (child != null) {
            this.addView(child);
        }
    }

    public void setToolsBarEnabled(boolean enabled) {
        final View child = this.mToolsBar;
        if (child != null) {
            child.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public void setToolsBarId(@IdRes int id) {
        final View child = this.mToolsBar;
        if (child != null) {
            child.setId(id);
        }
    }

    public void setToolsBarBackground(@Nullable Drawable drawable) {
        final View child = this.mToolsBar;
        if (child != null) {
            child.setBackground(drawable);
        }
    }

    public void setToolsBarBackgroundColor(@ColorInt int color) {
        final View child = this.mToolsBar;
        if (child != null) {
            child.setBackgroundColor(color);
        }
    }

    public void setToolsBarBackgroundResource(@DrawableRes int resId) {
        final View child = this.mToolsBar;
        if (child != null) {
            child.setBackgroundResource(resId);
        }
    }

    public void setToolsBarBackgroundAlpha(@FloatRange(from = 0, to = 1.f) float alpha) {
        final View child = this.mToolsBar;
        if (child != null) {
            Drawable backgroundDrawable = child.getBackground();
            if (backgroundDrawable == null) {
                backgroundDrawable = new ColorDrawable(Color.WHITE);
            }
            backgroundDrawable.setAlpha((int) (255 * alpha));
            child.setBackground(backgroundDrawable);
        }
    }

    public int getToolsBarWidth() {
        final View child = this.mToolsBar;
        if (child != null) {
            return child.getMeasuredWidth();
        }
        return 0;
    }

    public int getToolsBarHeight() {
        final View child = this.mToolsBar;
        if (child != null) {
            return child.getMeasuredHeight();
        }
        return 0;
    }

    @Nullable
    public Drawable getToolsBarBackground() {
        final View child = this.mToolsBar;
        return child == null
                ? null
                : child.getBackground();
    }

    @Nullable
    public View getToolsBar() {
        return this.mToolsBar;
    }
}
