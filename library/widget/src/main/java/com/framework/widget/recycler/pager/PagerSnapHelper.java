package com.framework.widget.recycler.pager;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2024-01-05
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PagerSnapHelper extends RecyclerView.OnFlingListener {
    public static final float MILLISECONDS_PER_INCH = 100f;
    public static final int MAX_SCROLL_ON_FLING_DURATION = 100;

    @NonNull
    private final int[] mOutDistances = new int[2];
    @NonNull
    private final DecelerateInterpolator
            mDecelerateInterpolator = new DecelerateInterpolator();
    @Nullable
    private Callback mCallback;
    @Nullable
    private RecyclerView mRecyclerView;
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;
    @Nullable
    private ComponentListener mComponentListener;
    private float mMillisPerPixel;
    private int mCurrentPosition = RecyclerView.NO_POSITION;

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        final RecyclerView oldRecyclerView = this.mRecyclerView;
        if (oldRecyclerView == recyclerView) {
            return;
        }
        if (oldRecyclerView != null) {
            oldRecyclerView.setOnFlingListener(null);
            if (this.mComponentListener != null) {
                oldRecyclerView.removeOnScrollListener(this.mComponentListener);
            }
        }
        this.mRecyclerView = recyclerView;
        this.mCurrentPosition = RecyclerView.NO_POSITION;
        if (recyclerView != null) {
            if (recyclerView.getOnFlingListener() != null) {
                throw new IllegalStateException(
                        "An instance of OnFlingListener already set.");
            }
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            recyclerView.setOnFlingListener(this);
            recyclerView.addOnScrollListener(this.mComponentListener);
            if (!this.snapToTargetExistingView(recyclerView)) {
                this.snapToTargetExistingPosition(recyclerView);
            }
        }
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return false;
        }
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) {
            return false;
        }
        final View snapView = this.findSnapView(lm, velocityX, velocityY);
        if (snapView == null) {
            return false;
        }
        return this.snapToTargetExistingView(recyclerView, snapView);
    }

    @Nullable
    public View findSnapView(@NonNull RecyclerView.LayoutManager layoutManager) {
        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        final OrientationHelper helper = this.getOrientationHelper(layoutManager);
        if (helper == null) {
            return null;
        }
        // A child that is exactly in the center is eligible for center
        View closestChild = null;
        int absClosest = Integer.MAX_VALUE;
        // Find views located near the center
        for (int index = 0; index < childCount; index++) {
            final View child = layoutManager.getChildAt(index);
            if (child == null) {
                continue;
            }
            final int distance = this.distanceToCenter(helper, child);
            final int absDistance = Math.abs(distance);
            /* if child center is closer than previous closest, set it as closest  */
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }

    @Nullable
    public View findSnapView(@NonNull RecyclerView.LayoutManager layoutManager,
                             int velocityX,
                             int velocityY) {
        final int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        final OrientationHelper helper = this.getOrientationHelper(layoutManager);
        if (helper == null) {
            return null;
        }
        final boolean forwardDir = this.isForwardFling(layoutManager, velocityX, velocityY);
        // A child that is exactly in the center is eligible for both before and after
        View closestChildBeforeCenter = null;
        int distanceBefore = Integer.MIN_VALUE;
        View closestChildAfterCenter = null;
        int distanceAfter = Integer.MAX_VALUE;
        // Find the first view before the center, and the first view after the center
        for (int index = 0; index < childCount; index++) {
            final View child = layoutManager.getChildAt(index);
            if (child == null) {
                continue;
            }
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
            if (containBefore && distance > distanceBefore) {
                // Child is before the center and closer then the previous best
                distanceBefore = distance;
                closestChildBeforeCenter = child;
            }
            if (containAfter && distance < distanceAfter) {
                // Child is after the center and closer then the previous best
                distanceAfter = distance;
                closestChildAfterCenter = child;
            }
        }
        // Return the position of the first child from the center, in the direction of the fling
        return forwardDir ? closestChildAfterCenter : closestChildBeforeCenter;
    }

    @Nullable
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        final int[] outDistances = this.mOutDistances;
        outDistances[0] = 0;
        outDistances[1] = 0;
        final OrientationHelper helper = this.getOrientationHelper(layoutManager);
        if (helper != null) {
            if (layoutManager.canScrollHorizontally()) {
                outDistances[0] = this.distanceToCenter(helper, targetView);
            }
            if (layoutManager.canScrollVertically()) {
                outDistances[1] = this.distanceToCenter(helper, targetView);
            }
        }
        return outDistances;
    }

    public int calculateTimeForDeceleration(@NonNull RecyclerView recyclerView,
                                            int distance) {
        final int time;
        time = this.calculateTimeForScrolling(recyclerView, distance);
        return (int) Math.ceil(time / .3356f);
    }

    public int calculateTimeForScrolling(@NonNull RecyclerView recyclerView,
                                         int distance) {
        if (this.mMillisPerPixel == 0.f) {
            this.mMillisPerPixel = MILLISECONDS_PER_INCH / recyclerView
                    .getResources()
                    .getDisplayMetrics().densityDpi;
        }
        final int time;
        time = (int) Math.ceil(Math.abs(distance) * this.mMillisPerPixel);
        return Math.min(MAX_SCROLL_ON_FLING_DURATION, time);
    }

    public boolean isForwardFling(@NonNull RecyclerView.LayoutManager layoutManager,
                                  int velocityX,
                                  int velocityY) {
        return layoutManager.canScrollHorizontally() ? velocityX > 0 : velocityY > 0;
    }

    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        final int oldPosition = this.mCurrentPosition;
        if (oldPosition == currentPosition) {
            return;
        }
        this.mCurrentPosition = currentPosition;
        if (currentPosition != RecyclerView.NO_POSITION) {
            this.dispatchOnPageSelected(currentPosition);
        }
    }

    @Nullable
    public Callback getCallback() {
        return this.mCallback;
    }

    public void setCallback(@Nullable Callback callback) {
        this.mCallback = callback;
    }

    public void dispatchOnScrolled(int dx, int dy) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mComponentListener != null) {
            this.mComponentListener.onScrolled(recyclerView, dx, dy);
        }
    }

    public void dispatchOnScrollStateChanged(int scrollState) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mComponentListener != null) {
            this.mComponentListener.onScrollStateChanged(recyclerView, scrollState);
        }
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

    private boolean snapToTargetExistingView(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) {
            return false;
        }
        final View snapView = this.findSnapView(lm);
        if (snapView == null) {
            return false;
        }
        return this.snapToTargetExistingView(recyclerView, snapView);
    }

    private boolean snapToTargetExistingView(@NonNull RecyclerView recyclerView,
                                             @NonNull View snapView) {
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) {
            return false;
        }
        final int[] snapDistance = this.calculateDistanceToFinalSnap(lm, snapView);
        if (snapDistance == null) {
            return false;
        }
        final int dx = snapDistance[0];
        final int dy = snapDistance[1];
        if (dx != 0 || dy != 0) {
            final int distance = Math.max(Math.abs(dx), Math.abs(dy));
            int duration = this.calculateTimeForDeceleration(recyclerView, distance);
            if (duration <= 0) {
                duration = RecyclerView.UNDEFINED_DURATION;
            }
            recyclerView.smoothScrollBy(dx, dy, this.mDecelerateInterpolator, duration);
        }
        return dx != 0 || dy != 0;
    }

    private void snapToTargetExistingPosition(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) {
            return;
        }
        final View snapView = this.findSnapView(lm);
        if (snapView == null) {
            return;
        }
        final int position = lm.getPosition(snapView);
        if (RecyclerView.NO_POSITION == position) {
            return;
        }
        if (this.mCurrentPosition != position) {
            this.mCurrentPosition = position;
            this.dispatchOnPageSelected(position);
        }
    }

    private void performOnPageScrolled(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) {
            return;
        }
        final View snapView = this.findSnapView(lm);
        if (snapView == null) {
            return;
        }
        final int position = lm.getPosition(snapView);
        if (RecyclerView.NO_POSITION == position) {
            return;
        }
        final int extent;
        final int scroll;
        if (lm.canScrollHorizontally()) {
            extent = recyclerView.computeHorizontalScrollExtent();
            scroll = recyclerView.computeHorizontalScrollOffset();
        } else {
            extent = recyclerView.computeVerticalScrollExtent();
            scroll = recyclerView.computeVerticalScrollOffset();
        }
        final float pageOffset = (float) scroll / extent;
        final float positionOffset = pageOffset - (float) position;
        final int positionOffsetPixels = (int) (positionOffset * extent);
        this.dispatchOnPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private void dispatchOnPageScrollStateChanged(int scrollState) {
        if (this.mCallback != null) {
            this.mCallback.onPageScrollStateChanged(scrollState);
        }
    }

    private void dispatchOnPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.mCallback != null) {
            this.mCallback.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    private void dispatchOnPageSelected(int position) {
        if (this.mCallback != null) {
            this.mCallback.onPageSelected(position);
        }
    }

    private final class ComponentListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
            if (RecyclerView.SCROLL_STATE_IDLE == recyclerView.getScrollState()) {
                if (!snapToTargetExistingView(recyclerView)) {
                    snapToTargetExistingPosition(recyclerView);
                }
            }
            if (recyclerView.getScrollState() == scrollState) {
                dispatchOnPageScrollStateChanged(scrollState);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            performOnPageScrolled(recyclerView);
            if (RecyclerView.SCROLL_STATE_IDLE == recyclerView.getScrollState()) {
                if (!snapToTargetExistingView(recyclerView)) {
                    snapToTargetExistingPosition(recyclerView);
                }
            }
        }
    }

    public interface Callback {
        void onPageScrollStateChanged(int scrollState);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);
    }
}
