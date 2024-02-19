package com.framework.widget.recycler.pager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DepthPageTransformer implements PagerLayoutManager.PageTransformer {
    private float mMinScale = 0.8f;
    private float mMinAlpha = 0.2f;

    @Override
    public void detachedFromParent(@NonNull RecyclerView recyclerView, @NonNull View itemView) {
        itemView.setAlpha(1.f);
        itemView.setPivotX(0.f);
        itemView.setPivotY(0.f);
        itemView.setScaleX(1.f);
        itemView.setScaleY(1.f);
        itemView.setTranslationX(0.f);
        itemView.setTranslationY(0.f);
    }

    @Override
    public void transformPage(@NonNull RecyclerView recyclerView,
                              @NonNull View itemView, float transformPos) {
        final PagerLayoutManager lm = (PagerLayoutManager) recyclerView.getLayoutManager();
        if (lm == null) {
            return;
        }
        final int width = lm.getWidth() - lm.getPaddingLeft() - lm.getPaddingRight();
        final int height = lm.getHeight() - lm.getPaddingTop() - lm.getPaddingBottom();
        final int childWidth = lm.getDecoratedMeasuredWidth(itemView);
        final int childHeight = lm.getDecoratedMeasuredHeight(itemView);

        final OrientationHelper orientationHelper = lm.requireOrientationHelper();
        final int decorSize = orientationHelper.getDecoratedMeasurement(itemView);
        final int totalSpace = orientationHelper.getTotalSpace();
        final float transformPosX = transformPos - ((((float) width - childWidth) / 2.f) / width);
        final float transformPosY = transformPos - ((((float) height - childHeight) / 2.f) / height);
        final float cTransformPos = transformPos - ((((float) totalSpace - decorSize) / 2.f) / totalSpace);

        if (0.f <= cTransformPos && cTransformPos < 1.f) {
            final float scaleFactorX = this.mMinScale + (1.f - this.mMinScale) * (1.f - Math.abs(transformPosX));
            final float scaleFactorY = this.mMinScale + (1.f - this.mMinScale) * (1.f - Math.abs(transformPosY));
            final float alphaFactor = this.mMinAlpha + (1.f - this.mMinAlpha) * (1.f - Math.abs(cTransformPos));
            itemView.setAlpha(alphaFactor);
            itemView.setPivotX(0.5f * childWidth);
            itemView.setPivotY(0.5f * childHeight);
            itemView.setScaleX(scaleFactorX);
            itemView.setScaleY(scaleFactorY);

            if (lm.canScrollHorizontally()) {
                itemView.setTranslationX(childWidth * -cTransformPos);
            } else {
                itemView.setTranslationY(childHeight * -cTransformPos);
            }
        } else {
            itemView.setAlpha(1.f);
            itemView.setPivotX(0.f);
            itemView.setPivotY(0.f);
            itemView.setScaleX(1.f);
            itemView.setScaleY(1.f);
            itemView.setTranslationX(0.f);
            itemView.setTranslationY(0.f);
        }
    }

    public void setMinScale(float minScale) {
        this.mMinScale = minScale;
    }

    public void setMinAlpha(float minAlpha) {
        this.mMinAlpha = minAlpha;
    }
}
