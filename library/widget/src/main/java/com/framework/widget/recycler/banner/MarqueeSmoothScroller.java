package com.framework.widget.recycler.banner;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.framework.widget.recycler.pager.PagerSmoothScroller;

/**
 * @Author create by Zhengzelong on 2023-03-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MarqueeSmoothScroller extends PagerSmoothScroller {
    private float mMarqueeSpeedPixel = 500.f;

    public MarqueeSmoothScroller(@NonNull Context context) {
        super(context);
    }

    @Override
    public float calculateSpeedPerPixel(@NonNull DisplayMetrics displayMetrics) {
        return this.mMarqueeSpeedPixel / displayMetrics.densityDpi;
    }

    public float getMarqueeSpeedPixel() {
        return this.mMarqueeSpeedPixel;
    }

    public void setMarqueeSpeedPixel(float marqueeSpeedPixel /*px*/) {
        this.mMarqueeSpeedPixel = marqueeSpeedPixel;
    }
}
