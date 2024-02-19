package com.framework.widget.recycler.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.recycler.pager.PagerLayoutManager;

/**
 * @Author create by Zhengzelong on 2023-03-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PickerLayoutManager extends PagerLayoutManager {
    /**
     * 阻尼消耗偏移量
     */
    private int mScrollConsumed;
    /**
     * 是否开启阻尼滑动
     */
    private boolean mBounceScrollEnabled;

    public PickerLayoutManager(@NonNull Context context) {
        this(context, VERTICAL);
    }

    public PickerLayoutManager(@NonNull Context context, int orientation) {
        this(context, orientation, false);
    }

    public PickerLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.setBounceScrollEnabled(true);
        this.setPageTransformer(new PageTransformer() {
        });
    }

    public PickerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PickerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setBounceScrollEnabled(true);
        this.setPageTransformer(new PageTransformer() {
        });
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        recyclerView.setOnFlingListener(null);
    }

    @Override
    public void onLayoutChildren(@NonNull RecyclerView.Recycler recycler,
                                 @NonNull RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        this.snapToTargetExistingView(recycler, state);
    }

    @Override
    public void onLayoutCompleted(@NonNull RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (this.mScrollConsumed != 0) {
            final OrientationHelper orientationHelper;
            orientationHelper = this.requireOrientationHelper();
            orientationHelper.offsetChildren(-this.mScrollConsumed);
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx,
                                    @NonNull RecyclerView.Recycler recycler,
                                    @NonNull RecyclerView.State state) {
        int scrolled;
        int consumed = 0;
        int unconsumed = 0;
        if (HORIZONTAL == this.getOrientation()) {
            unconsumed = dx - consumed;
        }
        if (unconsumed != 0) {
            scrolled = this.preBounceBy(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = super.scrollHorizontallyBy(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = this.scrollBy2(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = this.bounceBy(unconsumed, recycler, state);
            consumed += scrolled;
        }
        return consumed;
    }

    @Override
    public int scrollVerticallyBy(int dy,
                                  @NonNull RecyclerView.Recycler recycler,
                                  @NonNull RecyclerView.State state) {
        int scrolled;
        int consumed = 0;
        int unconsumed = 0;
        if (VERTICAL == this.getOrientation()) {
            unconsumed = dy - consumed;
        }
        if (unconsumed != 0) {
            scrolled = this.preBounceBy(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = super.scrollVerticallyBy(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = this.scrollBy2(unconsumed, recycler, state);
            consumed += scrolled;
            unconsumed -= scrolled;
        }
        if (unconsumed != 0) {
            scrolled = this.bounceBy(unconsumed, recycler, state);
            consumed += scrolled;
        }
        return consumed;
    }

    private int preBounceBy(int delta,
                            @NonNull RecyclerView.Recycler recycler,
                            @NonNull RecyclerView.State state) {
        if (this.getChildCount() == 0) {
            return 0;
        }
        final int scroll = this.mScrollConsumed;
        int consumed = 0;
        if (delta > 0 && scroll < 0) {
            consumed = Math.min(delta, -scroll);
        }
        if (delta < 0 && scroll > 0) {
            consumed = Math.max(delta, -scroll);
        }
        if (consumed == 0) {
            return 0;
        }
        final OrientationHelper orientationHelper;
        orientationHelper = this.requireOrientationHelper();
        orientationHelper.offsetChildren(-consumed);
        this.mScrollConsumed += consumed;
        return consumed;
    }

    private int scrollBy2(int delta,
                          @NonNull RecyclerView.Recycler recycler,
                          @NonNull RecyclerView.State state) {
        if (this.getChildCount() == 0) {
            return 0;
        }
        final View targetView;
        if (delta < 0) {
            targetView = this.getChildClosestToStart();
        } else {
            targetView = this.getChildClosestToEnd();
        }
        if (targetView == null) {
            return 0;
        }
        final int[] amounts = this.calculateDistanceToFinalSnap(targetView);
        if (amounts == null) {
            return 0;
        }
        final int range;
        if (this.canScrollHorizontally()) {
            range = -amounts[0];
        } else {
            range = -amounts[1];
        }
        int consumed = 0;
        if (delta < 0 && range >= 0) {
            if (delta + range < 0) {
                consumed = Math.max(delta, -range);
            } else {
                consumed = delta;
            }
        }
        if (delta > 0 && range <= 0) {
            if (delta + range > 0) {
                consumed = Math.min(delta, -range);
            } else {
                consumed = delta;
            }
        }
        if (consumed == 0) {
            return 0;
        }
        final OrientationHelper orientationHelper;
        orientationHelper = this.requireOrientationHelper();
        orientationHelper.offsetChildren(-consumed);
        return consumed;
    }

    private int bounceBy(int delta,
                         @NonNull RecyclerView.Recycler recycler,
                         @NonNull RecyclerView.State state) {
        if (!this.mBounceScrollEnabled || this.getChildCount() == 0) {
            return 0;
        }
        final View targetView;
        if (delta < 0) {
            targetView = this.getChildClosestToStart();
        } else {
            targetView = this.getChildClosestToEnd();
        }
        if (targetView == null) {
            return 0;
        }
        final OrientationHelper orientationHelper;
        orientationHelper = this.requireOrientationHelper();
        final float totalSize = orientationHelper.getTotalSpace();
        final float oldScroll = this.mScrollConsumed;
        float damping;
        damping = Math.max(0.00f, 1.0f - Math.abs(oldScroll) / Math.max(1.f, totalSize));
        damping *= 0.15f;
        damping += 0.10f;
        final float newScroll = ((oldScroll / damping) + delta) * damping;
        final int consumed = (int) (newScroll - oldScroll);
        if (consumed != 0) {
            orientationHelper.offsetChildren(-consumed);
        }
        this.mScrollConsumed += consumed;
        if (Math.abs(consumed) > 0) {
            final RecyclerView recyclerView = this.getRecyclerView();
            if (recyclerView != null) {
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    recyclerView.stopScroll();
                }
            }
        }
        return delta;
    }

    public boolean isBounceScrollEnabled() {
        return this.mBounceScrollEnabled;
    }

    public void setBounceScrollEnabled(boolean enabled) {
        this.assertNotInLayoutOrScroll(null);
        if (this.mBounceScrollEnabled != enabled) {
            this.mBounceScrollEnabled = enabled;
        }
    }

    public static abstract class PageTransformer implements PagerLayoutManager.PageTransformer {
        @Override
        public void detachedFromParent(@NonNull RecyclerView recyclerView,
                                       @NonNull View itemView) {
            itemView.setAlpha(1.f);
            itemView.setRotationX(0.f);
            itemView.setTranslationY(0.f);
            itemView.setTranslationZ(0.f);
        }

        @Override
        public void transformPage(@NonNull RecyclerView recyclerView,
                                  @NonNull View itemView, float transformPos) {
            final PickerLayoutManager lm = (PickerLayoutManager) recyclerView.getLayoutManager();
            if (lm == null) {
                return;
            }
            final OrientationHelper helper = lm.requireOrientationHelper();
            final float totalSize = helper.getTotalSpace();
            final float childHead = helper.getDecoratedStart(itemView);
            final float childSize = helper.getDecoratedMeasurement(itemView);
            if (totalSize == 0) {
                return;
            }
            // parent centerY
            final float parentCenterY = totalSize / 2.f;
            // item centerY
            final float childCenterY = childHead + childSize / 2.f;
            // item factor
            final float factor = (parentCenterY - childCenterY) * 1.f / parentCenterY;
            // item alpha
            final float alphaFactor = 1.f - 0.7f * Math.abs(factor);
            // set alpha
            itemView.setAlpha(alphaFactor * alphaFactor);

            // parent radius
            final float parentRadius = 6.f * parentCenterY / (float) Math.PI;
            // rotate radius
            final float rotateRadius = (parentCenterY - childCenterY) * 1.f / parentRadius;
            // rotate deg
            final float rotateDeg = rotateRadius * 180.f / (float) Math.PI;
            // for camera
            final float offsetZ = parentRadius * (1.f - (float) Math.cos(rotateRadius));
            // set rotate
            itemView.setRotationX(rotateDeg);
            // set z
            itemView.setTranslationZ(offsetZ);

            // offset Y for item rotate
            // final float offsetY = parentCenterY - childCenterY - parentRadius * (float) Math.sin(rotateRadius);
            // itemView.setTranslationY(offsetY);

            if (itemView instanceof ClipChildCallback) {
                // center top
                final int topBound = (int) (parentCenterY - (childSize / 2.f));
                // center bottom
                final int bottomBound = (int) (parentCenterY + (childSize / 2.f));
                // child clip boundary
                final int left = 0;
                final int right = left + itemView.getWidth();
                final int top = (int) Math.max(0, topBound - childHead);
                final int bottom = (int) Math.max(0, bottomBound - childHead);
                ((ClipChildCallback) itemView).dispatchClipChildChanged(recyclerView, left, top, right, bottom);
            }
        }
    }

    public interface ClipChildCallback {

        void dispatchClipChildChanged(@NonNull ViewGroup target, int left, int top, int right, int bottom);
    }
}
