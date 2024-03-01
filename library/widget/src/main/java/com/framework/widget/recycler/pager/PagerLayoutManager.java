package com.framework.widget.recycler.pager;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.view.AbsSavedState;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.recycler.LinearLayoutManager;

import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2023-03-24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PagerLayoutManager extends LinearLayoutManager implements
        RecyclerView.OnChildAttachStateChangeListener,
        RecyclerView.ChildDrawingOrderCallback {
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    /**
     * 缓存滑动监听事件
     */
    private final ArrayList<OnPageChangeListener>
            mOnPageChangeListeners = new ArrayList<>();
    /**
     * 绘制顺序
     */
    private int mDrawingOrder = DRAW_ORDER_DEFAULT;
    /**
     * 预期位置
     */
    private int mPendingPosition = RecyclerView.NO_POSITION;
    /**
     * 预览页数
     */
    private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;
    /**
     * 视图层类型
     */
    private int mPageTransformerLayerType = View.LAYER_TYPE_NONE;
    /**
     * 是否开启视图绘制排序
     */
    private boolean mChildrenDrawingOrderEnabled;
    /**
     * 父类
     */
    private RecyclerView mRecyclerView;
    /**
     * 页面转换器
     */
    private PageTransformer mPageTransformer;
    /**
     * 页面助手
     */
    private PagerSnapHelper mPagerSnapHelper;

    public PagerLayoutManager(@NonNull Context context) {
        this(context, HORIZONTAL);
    }

    public PagerLayoutManager(@NonNull Context context, int orientation) {
        this(context, orientation, false);
    }

    public PagerLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PagerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PagerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onAttachedToWindow(@NonNull RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        this.mRecyclerView = recyclerView;
        if (this.mChildrenDrawingOrderEnabled) {
            this.mRecyclerView.setChildDrawingOrderCallback(this);
        }
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        this.mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);
        this.mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        if (this.mPagerSnapHelper == null) {
            this.mPagerSnapHelper = new PagerSnapHelper();
            this.mPagerSnapHelper.setCallback(new PagerSnapCallback());
        }
        this.mPagerSnapHelper.attachToRecyclerView(recyclerView);
        this.restorePendingState();
    }

    @Override
    public void onDetachedFromWindow(@NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        this.mPagerSnapHelper.attachToRecyclerView(null);
        this.mRecyclerView.setChildDrawingOrderCallback(null);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        this.mRecyclerView = null;
    }

    @Override
    public void onAdapterChanged(@Nullable RecyclerView.Adapter oldAdapter,
                                 @Nullable RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        this.restorePendingState();
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View itemView) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mPageTransformer != null) {
            this.mPageTransformer.attachedToParent(recyclerView, itemView);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View itemView) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mPageTransformer != null) {
            this.mPageTransformer.detachedFromParent(recyclerView, itemView);
        }
    }

    @Override
    public void onLayoutCompleted(@NonNull RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (state.isPreLayout() || state.hasTargetScrollPosition()) {
            return;
        }
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mPageTransformer != null) {
            this.dispatchOnPageTransformer(recyclerView);
        }
    }

    @Override
    public int onGetChildDrawingOrder(int childCount, int index) {
        int realIndex = index;
        if (DRAW_ORDER_REVERSE == this.mDrawingOrder) {
            realIndex = childCount - 1 - index;
        }
        return realIndex;
    }

    @Override
    protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state,
                                             @NonNull int[] extraLayoutSpaces) {
        final int limit = this.mOffscreenPageLimit;
        int extraLayoutSpace = 0;
        if (this.canScrollVertically()) {
            extraLayoutSpace += this.getHeight();
            extraLayoutSpace -= this.getPaddingTop();
            extraLayoutSpace -= this.getPaddingBottom();
        } else {
            extraLayoutSpace += this.getWidth();
            extraLayoutSpace -= this.getPaddingLeft();
            extraLayoutSpace -= this.getPaddingRight();
        }
        extraLayoutSpaces[0] = (extraLayoutSpace * limit);
        extraLayoutSpaces[1] = (extraLayoutSpace * limit);
    }

    @Override
    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.State state, int position) {
        final RecyclerView.SmoothScroller smoothScroller;
        smoothScroller = new PagerSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        this.startSmoothScroll(smoothScroller);
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView recyclerView,
                                                 @NonNull View itemView,
                                                 @NonNull Rect rect,
                                                 boolean immediate,
                                                 boolean focusedChildVisible) {
        return false; // users should use setCurrentItem instead
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        int currentPosition = this.mPendingPosition;
        if (currentPosition == RecyclerView.NO_POSITION) {
            currentPosition = this.getCurrentPosition();
        }
        savedState.mCurrentPosition = currentPosition;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mPendingPosition = savedState.mCurrentPosition;
            this.restorePendingState();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void onPageScrollStateChanged(int scrollState) {
        // no-op
    }

    public void onPageSelected(int position) {
        // no-op
    }

    public int getCurrentPosition() {
        final PagerSnapHelper pagerSnapHelper = this.mPagerSnapHelper;
        if (pagerSnapHelper == null) {
            return RecyclerView.NO_POSITION;
        }
        return pagerSnapHelper.getCurrentPosition();
    }

    public void setCurrentPosition(int position) {
        this.setCurrentPosition(position, true);
    }

    public void setCurrentPosition(int position, boolean smoothScroll) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            this.mPendingPosition = position;
            return;
        }
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            this.mPendingPosition = position;
            return;
        }
        if (position < 0 || position >= adapter.getItemCount()) {
            return;
        }
        if (smoothScroll) {
            recyclerView.smoothScrollToPosition(position);
        } else {
            this.mPagerSnapHelper.setCurrentPosition(position);
            this.scrollToPositionWithOffset(position, 0);
        }
    }

    public void setCurrentItemView(@NonNull View itemView) {
        this.setCurrentItemView(itemView, true);
    }

    public void setCurrentItemView(@NonNull View itemView, boolean smoothScroll) {
        final int position = this.getPosition(itemView);
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            this.mPendingPosition = position;
            return;
        }
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            this.mPendingPosition = position;
            return;
        }
        if (position < 0 || position >= adapter.getItemCount()) {
            return;
        }
        if (smoothScroll) {
            final int[] snapDistance = this.calculateDistanceToFinalSnap(itemView);
            if (snapDistance == null) {
                return;
            }
            final int dx = snapDistance[0];
            final int dy = snapDistance[1];
            if (dx != 0 || dy != 0) {
                recyclerView.smoothScrollBy(dx, dy);
            }
        } else {
            this.setCurrentPosition(position, false);
        }
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    public void setOffscreenPageLimit(int limit) {
        if (limit < DEFAULT_OFFSCREEN_PAGES) {
            limit = DEFAULT_OFFSCREEN_PAGES;
        }
        this.assertNotInLayoutOrScroll(null);
        if (this.mOffscreenPageLimit != limit) {
            this.mOffscreenPageLimit = limit;
            this.requestLayout();
        }
    }

    public boolean isChildrenDrawingOrderEnabled() {
        return this.mChildrenDrawingOrderEnabled;
    }

    public void setChildrenDrawingOrderEnabled(boolean enabled) {
        this.assertNotInLayoutOrScroll(null);
        if (this.mChildrenDrawingOrderEnabled != enabled) {
            this.mChildrenDrawingOrderEnabled = enabled;

            final RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                if (enabled) {
                    recyclerView.setChildDrawingOrderCallback(this);
                } else {
                    recyclerView.setChildDrawingOrderCallback(null);
                }
            }
        }
    }

    @Nullable
    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    @Nullable
    public PagerSnapHelper getPagerSnapHelper() {
        return this.mPagerSnapHelper;
    }

    @Nullable
    public PageTransformer getPageTransformer() {
        return this.mPageTransformer;
    }

    public void setPageTransformer(@Nullable PageTransformer transformer) {
        this.setPageTransformer(transformer, !this.getReverseLayout());
    }

    public void setPageTransformer(@Nullable PageTransformer transformer,
                                   boolean reverseDrawingOrder) {
        this.setPageTransformer(transformer, reverseDrawingOrder, View.LAYER_TYPE_HARDWARE);
    }

    public void setPageTransformer(@Nullable PageTransformer transformer,
                                   boolean reverseDrawingOrder, int pageLayerType) {
        this.assertNotInLayoutOrScroll(null);
        final PageTransformer oldTransformer = this.mPageTransformer;
        if (oldTransformer == transformer) {
            return;
        }
        if (oldTransformer != null) {
            this.mDrawingOrder = DRAW_ORDER_DEFAULT;
        }
        this.mPageTransformer = transformer;
        this.setChildrenDrawingOrderEnabled(false);
        if (transformer != null) {
            this.mDrawingOrder = reverseDrawingOrder
                    ? DRAW_ORDER_REVERSE
                    : DRAW_ORDER_FORWARD;
            this.mPageTransformerLayerType = pageLayerType;
            this.setChildrenDrawingOrderEnabled(true);
        }
    }

    public void clearOnPageChangeListeners() {
        this.mOnPageChangeListeners.clear();
    }

    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        this.mOnPageChangeListeners.add(listener);
    }

    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        this.mOnPageChangeListeners.remove(listener);
    }

    @Nullable
    public View findSnapView() {
        final PagerSnapHelper pagerSnapHelper = this.mPagerSnapHelper;
        if (pagerSnapHelper == null) {
            return null;
        }
        return pagerSnapHelper.findSnapView(this);
    }

    @Nullable
    public int[] calculateDistanceToFinalSnap(@NonNull View itemView) {
        final PagerSnapHelper pagerSnapHelper = this.mPagerSnapHelper;
        if (pagerSnapHelper == null) {
            return null;
        }
        return pagerSnapHelper.calculateDistanceToFinalSnap(this, itemView);
    }

    protected void snapToTargetExistingView(@NonNull RecyclerView.Recycler recycler,
                                            @NonNull RecyclerView.State state) {
        if (state.isPreLayout() || state.hasTargetScrollPosition()) {
            return;
        }
        int position = this.getCurrentPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = 0;
        }
        final View child = this.findViewByPosition(position);
        if (child == null) {
            return;
        }
        final int[] amounts = this.calculateDistanceToFinalSnap(child);
        if (amounts == null) {
            return;
        }
        final int dx = amounts[0];
        final int dy = amounts[1];
        if (dx == 0 && dy == 0) {
            return;
        }
        int dxConsumed = 0;
        int dyConsumed = 0;
        if (this.canScrollHorizontally()) {
            dxConsumed = this.scrollHorizontallyBy(dx, recycler, state);
        }
        if (this.canScrollVertically()) {
            dyConsumed = this.scrollVerticallyBy(dy, recycler, state);
        }
        if (dxConsumed != 0 || dyConsumed != 0) {
            this.mPagerSnapHelper.dispatchOnScrolled(dxConsumed, dyConsumed);
        }
    }

    private void restorePendingState() {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        int pendingPosition = this.mPendingPosition;
        if (pendingPosition == RecyclerView.NO_POSITION) {
            return;
        }
        this.mPendingPosition = RecyclerView.NO_POSITION;
        final int itemCount = this.getItemCount();
        pendingPosition = Math.min(pendingPosition, itemCount - 1);
        pendingPosition = Math.max(0, pendingPosition);
        if (pendingPosition == this.getCurrentPosition()) {
            return;
        }
        this.mPagerSnapHelper.setCurrentPosition(pendingPosition);
        this.scrollToPositionWithOffset(pendingPosition, 0);
    }

    private void enableChildLayerTypes(boolean enable) {
        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            int layerType = View.LAYER_TYPE_NONE;
            if (enable) {
                layerType = this.mPageTransformerLayerType;
            }
            child.setLayerType(layerType, null);
        }
    }

    private void dispatchOnPageTransformer(@NonNull RecyclerView recyclerView) {
        final int extent;
        if (this.canScrollHorizontally()) {
            extent = recyclerView.computeHorizontalScrollExtent();
        } else {
            extent = recyclerView.computeVerticalScrollExtent();
        }
        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            final float offset;
            if (this.canScrollHorizontally()) {
                offset = this.getDecoratedLeft(child);
            } else {
                offset = this.getDecoratedTop(child);
            }
            final float transformPos = offset / (float) extent;
            this.mPageTransformer.transformPage(recyclerView, child, transformPos);
        }
    }

    private void dispatchOnPageScrollStateChanged(int scrollState) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        // Let the LayoutManager go first.
        this.onPageScrollStateChanged(scrollState);
        if (this.mPageTransformer != null) {
            // PageTransformers can do complex things that benefit from hardware layers.
            this.enableChildLayerTypes(RecyclerView.SCROLL_STATE_IDLE != scrollState);
        }
        for (OnPageChangeListener listener : this.mOnPageChangeListeners) {
            listener.onPageScrollStateChanged(recyclerView, scrollState);
        }
    }

    private void dispatchOnPageScrolled(int position,
                                        float positionOffset,
                                        int positionOffsetPixels) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        if (this.mPageTransformer != null) {
            this.dispatchOnPageTransformer(recyclerView);
        }
        for (OnPageChangeListener listener : this.mOnPageChangeListeners) {
            listener.onPageScrolled(recyclerView, position, positionOffset, positionOffsetPixels);
        }
    }

    private void dispatchOnPageSelected(int position) {
        final RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return;
        }
        // Let the LayoutManager go first.
        this.onPageSelected(position);
        for (OnPageChangeListener listener : this.mOnPageChangeListeners) {
            listener.onPageSelected(recyclerView, position);
        }
    }

    private final class PagerSnapCallback implements PagerSnapHelper.Callback {
        @Override
        public void onPageScrollStateChanged(int scrollState) {
            dispatchOnPageScrollStateChanged(scrollState);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            dispatchOnPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            dispatchOnPageSelected(position);
        }
    }

    public static class SavedState extends AbsSavedState {
        private int mCurrentPosition;

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mCurrentPosition = RecyclerView.NO_POSITION;
        }

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source parcel to read from
         */
        public SavedState(@NonNull Parcel source) {
            this(source, null);
        }

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source parcel to read from
         * @param loader ClassLoader to use for reading
         */
        public SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            this.mCurrentPosition = source.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mCurrentPosition);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            /**
             * Create a new instance of the Parcelable class, instantiating it
             * from the given Parcel whose data had previously been written by
             * {@link Parcelable#writeToParcel Parcelable.writeToParcel()} and
             * using the given ClassLoader.
             *
             * @param source The Parcel to read the object's data from.
             * @param loader The ClassLoader that this object is being created in.
             * @return Returns a new instance of the Parcelable class.
             */
            @Override
            public SavedState createFromParcel(@NonNull Parcel source, @Nullable ClassLoader loader) {
                return new SavedState(source, loader);
            }

            /**
             * Create a new instance of the Parcelable class, instantiating it
             * from the given Parcel whose data had previously been written by
             * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
             *
             * @param source The Parcel to read the object's data from.
             * @return Returns a new instance of the Parcelable class.
             */
            @Override
            public SavedState createFromParcel(@NonNull Parcel source) {
                return new SavedState(source);
            }

            /**
             * Create a new array of the Parcelable class.
             *
             * @param size Size of the array.
             * @return Returns an array of the Parcelable class, with every entry
             * initialized to null.
             */
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[0];
            }
        };
    }

    public interface PageTransformer {

        default void attachedToParent(@NonNull RecyclerView recyclerView, @NonNull View itemView) {
            // Here you can handle some initialization operations
        }

        default void detachedFromParent(@NonNull RecyclerView recyclerView, @NonNull View itemView) {
            // Here you can handle some restore/release operations
        }

        /**
         * @param transformPos 0 is one page position to the center.
         *                     1 is one page position to the right.
         *                     -1 is one page position to the left.
         */
        void transformPage(@NonNull RecyclerView recyclerView, @NonNull View itemView, float transformPos);
    }

    public interface OnPageChangeListener {

        default void onPageScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
            // nothing
        }

        default void onPageScrolled(@NonNull RecyclerView recyclerView, int position, float positionOffset, int positionOffsetPixels) {
            // nothing
        }

        default void onPageSelected(@NonNull RecyclerView recyclerView, int position) {
            // nothing
        }
    }
}
