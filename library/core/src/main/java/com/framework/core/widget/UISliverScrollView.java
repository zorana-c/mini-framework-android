package com.framework.core.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.framework.widget.sliver.SliverScrollView;

import java.io.File;

/**
 * @Author create by Zhengzelong on 2023-03-23
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UISliverScrollView extends SliverScrollView {

    public UISliverScrollView(@NonNull Context context) {
        this(context, null);
    }

    public UISliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UISliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setZoomImageUri(@NonNull Uri uri) {
        this.setZoomImage(uri);
    }

    public void setZoomImageUrl(@NonNull String url) {
        this.setZoomImage(url);
    }

    public void setZoomImageFile(@NonNull File file) {
        this.setZoomImage(file);
    }

    public void setZoomImageFile(@NonNull byte[] bytes) {
        this.setZoomImage(bytes);
    }

    public void setZoomImage(@NonNull Object object) {
        Glide.with(this)
                .load(object)
                .into(UISliverViewTarget.get(this));
    }
}
