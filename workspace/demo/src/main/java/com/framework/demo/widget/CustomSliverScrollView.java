package com.framework.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.compat.UIRes;
import com.framework.core.widget.UISliverScrollView;
import com.framework.widget.sliver.SliverCompat;
import com.framework.widget.sliver.SliverContainer;
import com.framework.widget.sliver.ViewScroller;

/**
 * @Author create by Zhengzelong on 2023-07-21
 * @Email : 171905184@qq.com
 * @Description : 惯性回弹效果
 */
public class CustomSliverScrollView extends UISliverScrollView {
    public CustomSliverScrollView(@NonNull Context context) {
        this(context, null);
    }

    public CustomSliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSliverScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnFlingListener(new ComponentListener());
    }

    @Override
    public void onBounceScroll(int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, @NonNull int[] consumed, int scrollType) {
        if (SliverCompat.TYPE_TOUCH == scrollType) {
            super.onBounceScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, consumed, scrollType);
        } else {
            this.scrollBy(dxUnconsumed, dyUnconsumed);
            consumed[0] += dxUnconsumed;
            consumed[1] += dyUnconsumed;
        }
    }

    @Override
    public boolean onSliverPreFling(float velocityX, float velocityY) {
        return false;
    }

    private static final class ComponentListener implements SliverContainer.OnFlingListener {
        @Override
        public boolean onFling(@NonNull SliverContainer sliverContainer, int velocityX, int velocityY) {
            final int ori = sliverContainer.getOrientation();
            final int min = sliverContainer.getMinFlingVelocity();
            final int max = sliverContainer.getMaxFlingVelocity();
            if (Math.abs(velocityX) < min) {
                velocityX = 0;
            }
            if (Math.abs(velocityY) < min) {
                velocityY = 0;
            }
            if (velocityX == 0 && velocityY == 0) {
                return false;
            }
            int scrollAxes = SliverCompat.SCROLL_AXIS_NONE;
            if (SliverContainer.VERTICAL == ori) {
                scrollAxes |= SliverCompat.SCROLL_AXIS_VERTICAL;
            }
            if (SliverContainer.HORIZONTAL == ori) {
                scrollAxes |= SliverCompat.SCROLL_AXIS_HORIZONTAL;
            }
            sliverContainer.startSliverScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            sliverContainer.startNestedScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            sliverContainer.startInheritScroll(scrollAxes, SliverCompat.TYPE_NON_TOUCH);
            velocityX = Math.max(-max, Math.min(velocityX, max));
            velocityY = Math.max(-max, Math.min(velocityY, max));

            final int x_r = sliverContainer.computeHorizontalScrollRange()
                    - sliverContainer.computeHorizontalScrollExtent();
            final int y_r = sliverContainer.computeVerticalScrollRange()
                    - sliverContainer.computeVerticalScrollExtent();
            final int x_s = sliverContainer.computeHorizontalScrollOffset();
            final int y_s = sliverContainer.computeVerticalScrollOffset();
            final int x_v = velocityX < 0 ? -x_s : x_r - x_s;
            final int y_v = velocityY < 0 ? -y_s : y_r - y_s;

            final int x_t = sliverContainer.getExtraOffsetX();
            final int y_t = sliverContainer.getExtraOffsetY();
            int startX = 0;
            int startY = 0;

            int minX = Integer.MIN_VALUE;
            int maxX = Integer.MAX_VALUE;

            int minY = Integer.MIN_VALUE;
            int maxY = Integer.MAX_VALUE;

            final int overX;
            final int overY;

            final ViewScroller vs = sliverContainer.getViewScroller();
            final OverScroller os = vs.getScroller();
            os.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            os.abortAnimation();

            int x_f = os.getFinalX();
            int y_f = os.getFinalY();

            if (x_f < 0) {
                if (x_f <= -x_t && x_t >= 0) {
                    x_f = Math.min(Math.max(x_f, x_v), -x_t);
                } else x_f = 0;
            }
            if (x_f > 0) {
                if (x_f >= -x_t && x_t <= 0) {
                    x_f = Math.max(Math.min(x_f, x_v), -x_t);
                } else x_f = 0;
            }

            if (y_f < 0) {
                if (y_f <= -y_t && y_t >= 0) {
                    y_f = Math.min(Math.max(y_f, y_v), -y_t);
                } else y_f = 0;
            }
            if (y_f > 0) {
                if (y_f >= -y_t && y_t <= 0) {
                    y_f = Math.max(Math.min(y_f, y_v), -y_t);
                } else y_f = 0;
            }

            if (x_f == 0 && y_f == 0) {
                return sliverContainer.smoothScrollBy(-x_t, -y_t);
            }

            minX = velocityX < 0 ? Math.min(-x_s, 0) : startX;
            maxX = velocityX < 0 ? startX : Math.max(0, x_r - x_s);

            minY = velocityY < 0 ? Math.min(-y_s, 0) : startY;
            maxY = velocityY < 0 ? startY : Math.max(0, y_r - y_s);

            final int over = UIRes.dip2px(sliverContainer.getContext(), 50);
            overX = minX == 0 && maxX == startX ? 0 : over;
            overY = minY == 0 && maxY == startY ? 0 : over;
            return sliverContainer.flingBy(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
        }
    }
}
