package com.framework.core.widget;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.framework.widget.sliver.SliverScrollView;

/**
 * @Author create by Zhengzelong on 2023-03-14
 * @Email : 171905184@qq.com
 * @Description : 加载网络图片
 */
public abstract class UISliverViewTarget<T> extends CustomViewTarget<SliverScrollView, T>
        implements Transition.ViewAdapter {

    @NonNull
    public static UISliverViewTarget<Drawable> get(@NonNull SliverScrollView sliverScrollView) {
        return new UISliverViewTarget<Drawable>(sliverScrollView) {
            @Override
            public void setResource(@Nullable Drawable resource) {
                this.view.setZoomImageDrawable(resource);
            }
        };
    }

    @Nullable
    private Animatable animatable;

    public UISliverViewTarget(@NonNull SliverScrollView sliverScrollView) {
        super(sliverScrollView);
    }

    @Override
    public void onResourceReady(@NonNull T resource, @Nullable Transition<? super T> transition) {
        if (transition == null || !transition.transition(resource, this)) {
            this.setResourceInternal(resource);
        } else {
            this.maybeUpdateAnimatable(resource);
        }
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        this.setResourceInternal(null);
        this.setDrawable(errorDrawable);
    }

    @Override
    public void onResourceCleared(@Nullable Drawable placeholder) {
        if (this.animatable != null) {
            this.animatable.stop();
        }
        this.setResourceInternal(null);
        this.setDrawable(placeholder);
    }

    public abstract void setResource(@Nullable T resource);

    @Override
    public void onStart() {
        super.onStart();
        if (this.animatable != null) {
            this.animatable.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.animatable != null) {
            this.animatable.stop();
        }
    }

    @Nullable
    @Override
    public Drawable getCurrentDrawable() {
        return this.view.getZoomDrawable();
    }

    @Override
    public void setDrawable(@Nullable Drawable drawable) {
        this.view.setZoomImageDrawable(drawable);
    }

    private void setResourceInternal(@Nullable T resource) {
        this.setResource(resource);
        this.maybeUpdateAnimatable(resource);
    }

    private void maybeUpdateAnimatable(@Nullable T resource) {
        if (resource instanceof Animatable) {
            this.animatable = (Animatable) resource;
            this.animatable.start();
        } else {
            this.animatable = null;
        }
    }
}
