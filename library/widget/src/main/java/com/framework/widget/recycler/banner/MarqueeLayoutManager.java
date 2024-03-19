package com.framework.widget.recycler.banner;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MarqueeLayoutManager extends BannerLayoutManager {
    private float mMarqueeSpeedPixel = 500.f;

    public MarqueeLayoutManager(@NonNull Context context) {
        this(context, VERTICAL);
    }

    public MarqueeLayoutManager(@NonNull Context context, int orientation) {
        this(context, orientation, false);
    }

    public MarqueeLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MarqueeLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarqueeLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.State state, int position) {
        final MarqueeSmoothScroller smoothScroller;
        smoothScroller = new MarqueeSmoothScroller(recyclerView.getContext());
        smoothScroller.setMarqueeSpeedPixel(this.mMarqueeSpeedPixel);
        smoothScroller.setTargetPosition(position);
        this.startSmoothScroll(smoothScroller);
    }

    public float getMarqueeSpeedPixel() {
        return this.mMarqueeSpeedPixel;
    }

    public void setMarqueeSpeedPixel(float marqueeSpeedPixel /*px*/) {
        this.mMarqueeSpeedPixel = marqueeSpeedPixel;
    }
}
