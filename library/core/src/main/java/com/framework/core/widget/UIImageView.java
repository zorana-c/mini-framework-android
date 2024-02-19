package com.framework.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.framework.core.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

/**
 * @Author create by Zhengzelong on 2023-04-11
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIImageView extends RoundedImageView {
    @Nullable
    private Drawable mErrorDrawable;
    @Nullable
    private Drawable mFallbackDrawable;
    @Nullable
    private Drawable mPlaceholderDrawable;

    public UIImageView(@NonNull Context context) {
        this(context, null);
    }

    public UIImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.UIImageView);
            this.setErrorDrawable(typedArray.getDrawable(R.styleable.UIImageView_errorSrc));
            this.setPlaceholderDrawable(typedArray.getDrawable(R.styleable.UIImageView_placeholderSrc));
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    public void setImageUri(@Nullable Uri uri) {
        this.setImage(uri);
    }

    public void setImageUrl(@Nullable String url) {
        this.setImage(url);
    }

    public void setImageFile(@Nullable File file) {
        this.setImage(file);
    }

    public void setImageFile(@Nullable byte[] bytes) {
        this.setImage(bytes);
    }

    public void setImage(@Nullable Object object) {
        Glide.with(this)
                .load(object)
                .error(this.mErrorDrawable)
                .fallback(this.mFallbackDrawable)
                .placeholder(this.mPlaceholderDrawable)
                .into(this);
    }

    @Nullable
    public Drawable getErrorDrawable() {
        return this.mErrorDrawable;
    }

    public void setErrorColor(@ColorInt int color) {
        this.setErrorDrawable(new ColorDrawable(color));
    }

    public void setErrorColorRes(@ColorRes int colorResId) {
        final int color;
        color = ContextCompat.getColor(this.getContext(), colorResId);
        this.setErrorColor(color);
    }

    public void setErrorResource(@DrawableRes int resId) {
        final Drawable drawable;
        drawable = ContextCompat.getDrawable(this.getContext(), resId);
        this.setErrorDrawable(drawable);
    }

    public void setErrorDrawable(@Nullable Drawable drawable) {
        this.mErrorDrawable = drawable;
    }

    @Nullable
    public Drawable getFallbackDrawable() {
        return this.mFallbackDrawable;
    }

    public void setFallbackColor(@ColorInt int color) {
        this.setFallbackDrawable(new ColorDrawable(color));
    }

    public void setFallbackColorRes(@ColorRes int colorResId) {
        final int color;
        color = ContextCompat.getColor(this.getContext(), colorResId);
        this.setFallbackColor(color);
    }

    public void setFallbackResource(@DrawableRes int resId) {
        final Drawable drawable;
        drawable = ContextCompat.getDrawable(this.getContext(), resId);
        this.setFallbackDrawable(drawable);
    }

    public void setFallbackDrawable(@Nullable Drawable drawable) {
        this.mFallbackDrawable = drawable;
    }

    @Nullable
    public Drawable getPlaceholderDrawable() {
        return this.mPlaceholderDrawable;
    }

    public void setPlaceholderColor(@ColorInt int color) {
        this.setPlaceholderDrawable(new ColorDrawable(color));
    }

    public void setPlaceholderColorRes(@ColorRes int colorResId) {
        final int color;
        color = ContextCompat.getColor(this.getContext(), colorResId);
        this.setPlaceholderColor(color);
    }

    public void setPlaceholderResource(@DrawableRes int resId) {
        final Drawable drawable;
        drawable = ContextCompat.getDrawable(this.getContext(), resId);
        this.setPlaceholderDrawable(drawable);
    }

    public void setPlaceholderDrawable(@Nullable Drawable drawable) {
        this.mPlaceholderDrawable = drawable;
    }
}
