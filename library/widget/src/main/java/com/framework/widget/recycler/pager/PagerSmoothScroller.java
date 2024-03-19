package com.framework.widget.recycler.pager;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.recycler.LinearSmoothScroller;

/**
 * @Author create by Zhengzelong on 2023-03-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PagerSmoothScroller extends LinearSmoothScroller {
    public static final float MILLISECONDS_PER_INCH = 100f;
    public static final int MAX_SCROLL_ON_FLING_DURATION = 100;

    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    public PagerSmoothScroller(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getVerticalSnapPreference() {
        return SNAP_TO_CENTER;
    }

    @Override
    public int getHorizontalSnapPreference() {
        return SNAP_TO_CENTER;
    }

    @Override
    public int calculateTimeForScrolling(int dx) {
        final int time = super.calculateTimeForScrolling(dx);
        return Math.min(MAX_SCROLL_ON_FLING_DURATION, time);
    }

    @Override
    public float calculateSpeedPerPixel(@NonNull DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / (float) displayMetrics.densityDpi;
    }

    @Override
    public View findViewByPosition(int targetPosition) {
        final RecyclerView.LayoutManager lm = this.getLayoutManager();
        if (lm == null) {
            return super.findViewByPosition(targetPosition);
        }
        final int childCount = lm.getChildCount();
        if (childCount == 0) {
            return super.findViewByPosition(targetPosition);
        }
        final OrientationHelper helper = this.getOrientationHelper(lm);
        if (helper == null) {
            return super.findViewByPosition(targetPosition);
        }
        final boolean forwardDir = !this.isReverseLayout(lm);
        // A child that is exactly in the center is eligible for both before and after
        View closestChildBeforeCenter = null;
        int distanceBefore = Integer.MIN_VALUE;
        View closestChildAfterCenter = null;
        int distanceAfter = Integer.MAX_VALUE;
        // Find the first view before the center, and the first view after the center
        for (int index = 0; index < childCount; index++) {
            final View child = lm.getChildAt(index);
            if (child == null) {
                continue;
            }
            final RecyclerView.LayoutParams lp;
            lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            // Child distance in the center and closer then the previous best
            final int distance = this.distanceToCenter(helper, child);
            final boolean containBefore;
            final boolean containAfter;
            // Child direction in the center and closer then the previous best
            if (forwardDir) {
                containBefore = distance <= 0;
                containAfter = distance > 0;
            } else {
                containBefore = distance < 0;
                containAfter = distance >= 0;
            }
            // Child is before the center and closer then the previous best
            if (containBefore && distance > distanceBefore) {
                distanceBefore = distance;

                if (!lp.isItemRemoved()
                        && lp.getViewLayoutPosition() == targetPosition) {
                    closestChildBeforeCenter = child;
                }
            }
            // Child is after the center and closer then the previous best
            if (containAfter && distance < distanceAfter) {
                distanceAfter = distance;

                if (!lp.isItemRemoved()
                        && lp.getViewLayoutPosition() == targetPosition) {
                    closestChildAfterCenter = child;
                }
            }
        }
        // Return the position of the first child from the center
        return forwardDir ? closestChildAfterCenter : closestChildBeforeCenter;
    }

    public int distanceToCenter(@NonNull OrientationHelper helper,
                                @NonNull View targetView) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter = helper.getStartAfterPadding()
                + helper.getTotalSpace() / 2;
        return childCenter - containerCenter;
    }

    @Nullable
    public OrientationHelper getOrientationHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return this.getVerticalHelper(layoutManager);
        } else if (layoutManager.canScrollHorizontally()) {
            return this.getHorizontalHelper(layoutManager);
        }
        return null;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (this.mVerticalHelper == null
                || this.mVerticalHelper.getLayoutManager() != layoutManager) {
            this.mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return this.mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (this.mHorizontalHelper == null
                || this.mHorizontalHelper.getLayoutManager() != layoutManager) {
            this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return this.mHorizontalHelper;
    }
}
