package com.framework.core.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.framework.core.R;

/**
 * @Author create by Zoran on 2020/8/2
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class AppCompatStatusBar extends View {
    private final int mSystemStatusBarHeight;

    public AppCompatStatusBar(Context context) {
        this(context, null);
    }

    public AppCompatStatusBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSystemStatusBarHeight = this.getSystemStatusBarHeight();
        Drawable background = this.getBackground();
        if (background == null) {
            final int colorInt = ContextCompat.getColor(context, R.color.colorStatusBar);
            background = new ColorDrawable(colorInt);
        }
        this.setBackground(background);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                this.mSystemStatusBarHeight, MeasureSpec.EXACTLY));
    }

    @SuppressLint("DiscouragedApi, InternalInsetResource")
    public int getSystemStatusBarHeight() {
        final Resources resources = this.getResources();
        final int resourceId = resources.getIdentifier(
                "status_bar_height", "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }
}
