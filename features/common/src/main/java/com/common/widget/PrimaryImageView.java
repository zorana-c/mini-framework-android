package com.common.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.common.R;

/**
 * @Author create by Zhengzelong on 2024-02-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PrimaryImageView extends AppCompatImageView {
    @Nullable
    private ColorStateList mFocusColorStateList;
    @Nullable
    private ColorStateList mNormalColorStateList;

    public PrimaryImageView(@NonNull Context context) {
        this(context, null);
    }

    public PrimaryImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrimaryImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.PrimaryImageView);
        final boolean selected;
        selected = typedArray.getBoolean(R.styleable.PrimaryImageView_selected, false);
        int colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
        colorPrimary = typedArray.getColor(R.styleable.PrimaryImageView_colorPrimary, colorPrimary);
        typedArray.recycle();
        this.setPrimaryColor(colorPrimary);
        this.setSelected(selected);
    }

    @Override
    protected void dispatchSetSelected(boolean selected) {
        super.dispatchSetSelected(selected);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        if (this.mFocusColorStateList == null) {
            return;
        }
        if (selected) {
            this.mNormalColorStateList = this.getImageTintList();
        }
        if (selected) {
            this.setImageTintList(this.mFocusColorStateList);
        } else {
            this.setImageTintList(this.mNormalColorStateList);
        }
    }

    @Nullable
    public ColorStateList getPrimaryColorStateList() {
        return this.mFocusColorStateList;
    }

    public void setPrimaryColor(@ColorInt int colorInt) {
        this.setPrimaryColorStateList(ColorStateList.valueOf(colorInt));
    }

    public void setPrimaryColorStateList(@Nullable ColorStateList colorStateList) {
        this.mFocusColorStateList = colorStateList;
    }
}
