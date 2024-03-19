package com.framework.widget.expand;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author create by Zhengzelong on 2023-03-20
 * @Email : 171905184@qq.com
 * @Description : 粘性布局
 * <pre>
 * <com.framework.widget.expand.StickyRecyclerLayout
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent">
 *
 *      <com.framework.widget.expand.ExpandableRecyclerView
 *           android:layout_width="match_parent"
 *           android:layout_height="match_parent"/>
 *
 *      <.../>
 * </com.framework.widget.expand.StickyRecyclerLayout>
 * </pre>
 */
public class StickyRecyclerLayout extends FrameLayout {
    @Nullable
    private Component mComponent;
    private boolean mIsPushAnimationEnabled;

    public StickyRecyclerLayout(@NonNull Context context) {
        this(context, null);
    }

    public StickyRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setMeasureAllChildren(true);
        this.setPushAnimationEnabled(true);
    }

    @Override
    public void onViewAdded(@NonNull View child) {
        super.onViewAdded(child);
        if (child instanceof ExpandableRecyclerView) {
            if (this.mComponent == null) {
                this.mComponent = new Component(this);
                this.mComponent.bind((ExpandableRecyclerView) child);
            } else {
                throw new IllegalStateException("ERROR");
            }
        }
    }

    @Override
    public void onViewRemoved(@NonNull View child) {
        super.onViewRemoved(child);
        if (child instanceof ExpandableRecyclerView) {
            final ExpandableRecyclerView expandableRecyclerView;
            expandableRecyclerView = (ExpandableRecyclerView) child;
            this.clearAndRecycleViewHolder(expandableRecyclerView);
            if (this.mComponent != null) {
                this.mComponent.unbind(expandableRecyclerView);
                this.mComponent = null;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mComponent != null) {
            this.mComponent.dispatchFloating();
        }
        final RecyclerView.ViewHolder holder = this.mLastHolder;
        if (holder != null) {
            final View child = holder.itemView;
            if (child.getVisibility() == View.GONE) {
                return;
            }
            if (child.getParent() == this) {
                this.layoutStickChild(child, left, top, right, bottom);
            }
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width != oldWidth || height != oldHeight) {
            if (this.mComponent != null) {
                this.mComponent.dispatchFloating();
            }
        }
    }

    protected void layoutStickChild(@NonNull View child,
                                    int left, int top, int right, int bottom) {
        final int parentLeft = this.getPaddingLeft();
        final int parentRight = right - left - this.getPaddingRight();

        final int parentTop = this.getPaddingTop();
        final int parentBottom = bottom - top - this.getPaddingBottom();

        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();

        int gravity = lp.gravity;
        if (gravity == LayoutParams.UNSPECIFIED_GRAVITY) {
            gravity = Gravity.TOP | Gravity.START;
        }
        final int layoutDirection = this.getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int childLeft;
        final int childTop = parentTop + lp.topMargin;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = parentLeft + (parentRight - parentLeft - width) / 2
                        + lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.RIGHT:
                childLeft = parentRight - width - lp.rightMargin;
                break;
            case Gravity.LEFT:
            default:
                childLeft = parentLeft + lp.leftMargin;
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);
    }

    public void onViewChanged(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        // no-op
    }

    public void onViewRecycled(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        // no-op
    }

    public void onViewAttachedToWindow(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        // no-op
    }

    public void onViewDetachedFromWindow(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        // no-op
    }

    public final void postOnFloating() {
        if (this.mComponent != null) {
            this.mComponent.postOnFloating();
        }
    }

    public final void dispatchFloating() {
        if (this.mComponent != null) {
            this.mComponent.dispatchFloating();
        }
    }

    public final void dispatchDataSetChanged() {
        if (this.mComponent != null) {
            this.mComponent.dispatchDataSetChanged();
        }
    }

    public final boolean isPushAnimationEnabled() {
        return this.mIsPushAnimationEnabled;
    }

    public final void setPushAnimationEnabled(boolean enabled) {
        this.mIsPushAnimationEnabled = enabled;
    }

    @Nullable
    public final ExpandableRecyclerView.ViewHolder getPushHolder() {
        return this.mLastHolder;
    }

    public final int getDecoratedMeasuredWidth(@NonNull View child) {
        final MarginLayoutParams lp;
        lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    public final int getDecoratedMeasuredHeight(@NonNull View child) {
        final MarginLayoutParams lp;
        lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    @Nullable
    private ExpandableRecyclerView.ViewHolder mLastHolder;

    private void dispatchFloating(@NonNull ExpandableRecyclerView target) {
        final ExpandableRecyclerView.ViewHolder firstHolder;
        ExpandableRecyclerView.ViewHolder stickHolder;

        firstHolder = this.findFirstVisibleViewHolder(target);
        stickHolder = this.mLastHolder;

        final int firstGroupPosition = getGroupPosition(firstHolder);
        final int stickGroupPosition = getGroupPosition(stickHolder);

        if (firstHolder != null
                && firstGroupPosition != stickGroupPosition
                && firstGroupPosition != ExpandableRecyclerView.NO_POSITION) {
            final int firstItemPosition = this.swapItemPosition(firstHolder);
            final int firstItemViewType = this.swapItemViewType(firstHolder);
            final int stickItemViewType = getItemViewType(stickHolder);

            if (firstItemViewType != stickItemViewType) {
                if (this.removeAndRecycleViewHolder(target, stickHolder)) {
                    this.onViewRecycled(stickHolder);
                }
                stickHolder = this.hatchAndAddedViewHolder(target, firstItemViewType);
            }
            this.startingBindViewHolder(target, stickHolder, firstItemPosition);
        }
        this.mLastHolder = stickHolder;

        if (stickHolder == null) {
            return;
        }
        final View stickChild = stickHolder.itemView;
        if (ExpandableRecyclerView.NO_POSITION == firstGroupPosition) {
            stickChild.setVisibility(View.GONE);
            return;
        } else {
            stickChild.setVisibility(View.VISIBLE);
        }

        if (!this.isPushAnimationEnabled()) {
            return;
        }
        final ExpandableRecyclerView.LayoutManager lm = target.getLayoutManager();
        if (lm == null) {
            throw new IllegalStateException("ERROR");
        }
        final ExpandableRecyclerView.ViewHolder firstPreHolder;
        firstPreHolder = this.findFirstVisiblePreViewHolder(target, stickHolder);

        final int stickCurGroupPosition = getGroupPosition(stickHolder);
        final int firstPreGroupPosition = getGroupPosition(firstPreHolder);

        if (firstPreHolder != null
                && firstPreGroupPosition != stickCurGroupPosition
                && firstPreGroupPosition != ExpandableRecyclerView.NO_POSITION) {
            final View firstPreChild = firstPreHolder.itemView;
            final int scrollOffset = lm.getDecoratedTop(firstPreChild);
            final int scrollExtent = this.getDecoratedMeasuredHeight(stickChild);
            stickChild.setTranslationY(Math.min(0, scrollOffset - scrollExtent));
        } else {
            stickChild.setTranslationY(0);
        }
    }

    private void dispatchDataSetChanged(@NonNull ExpandableRecyclerView target) {
        if (target.isAnimating()) {
            return;
        }
        final int groupPosition = getGroupPosition(this.mLastHolder);
        if (ExpandableRecyclerView.NO_POSITION == groupPosition) {
            return;
        }
        final ExpandableAdapter<?> adapter = target.requireAdapter();
        final int adapterPosition;
        adapterPosition = adapter.getAdapterPositionByPositionType(PositionType.TYPE_GROUP, groupPosition);
        if (adapterPosition < 0 || adapterPosition >= adapter.getGroupItemCount()) {
            return;
        }
        this.startingBindViewHolder(target, this.mLastHolder, adapterPosition);
    }

    private int swapItemPosition(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final int positionType = holder.getPositionType();
        if (PositionType.TYPE_CHILD == positionType) {
            return holder.getLayoutPosition() - holder.getChildPosition() - 1;
        }
        return holder.getLayoutPosition();
    }

    private int swapItemViewType(@NonNull ExpandableRecyclerView.ViewHolder holder) {
        final int positionType = holder.getPositionType();
        if (PositionType.TYPE_GROUP == positionType) {
            return holder.getItemViewType();
        }
        final int layoutPosition = this.swapItemPosition(holder);
        return holder.requireAdapter().getItemViewType(layoutPosition);
    }

    private void clearAndRecycleViewHolder(@NonNull ExpandableRecyclerView target) {
        if (this.removeAndRecycleViewHolder(target, this.mLastHolder, false)) {
            this.mLastHolder = null;
        }
    }

    private boolean removeAndRecycleViewHolder(@NonNull ExpandableRecyclerView target,
                                               @Nullable ExpandableRecyclerView.ViewHolder holder) {
        return this.removeAndRecycleViewHolder(target, holder, true);
    }

    private boolean removeAndRecycleViewHolder(@NonNull ExpandableRecyclerView target,
                                               @Nullable ExpandableRecyclerView.ViewHolder holder,
                                               boolean recycleToViewsPool) {
        boolean needsRecycleViewHolder = false;
        if (holder != null) {
            final View itemView = holder.itemView;
            if (this == itemView.getParent()) {
                this.onViewDetachedFromWindow(holder);
                this.removeView(itemView);
                needsRecycleViewHolder = true;
            }
            if (needsRecycleViewHolder) {
                itemView.setTranslationY(0);
                itemView.setVisibility(View.VISIBLE);
            }
            holder.recycler();
        }
        if (recycleToViewsPool && needsRecycleViewHolder) {
            final ExpandableRecyclerView.RecycledViewPool pools;
            pools = target.getRecycledViewPool();
            pools.putRecycledView(holder);
        }
        return needsRecycleViewHolder;
    }

    private void startingBindViewHolder(@NonNull ExpandableRecyclerView target,
                                        @NonNull ExpandableRecyclerView.ViewHolder holder, int position) {
        final ExpandableRecyclerView.Adapter<ExpandableRecyclerView.ViewHolder> adapter = target.requireAdapter();
        adapter.bindViewHolder(holder, position);
        this.onViewChanged(holder);
    }

    @NonNull
    private ExpandableRecyclerView.ViewHolder hatchAndAddedViewHolder(@NonNull ExpandableRecyclerView target,
                                                                      int itemViewType) {
        final ExpandableRecyclerView.RecycledViewPool pools = target.getRecycledViewPool();
        ExpandableRecyclerView.ViewHolder result;
        result = (ExpandableRecyclerView.ViewHolder) pools.getRecycledView(itemViewType);
        if (result == null) {
            final ExpandableRecyclerView.Adapter<ExpandableRecyclerView.ViewHolder> adapter = target.requireAdapter();
            result = adapter.createViewHolder(target, itemViewType);
        }
        final View child = result.itemView;
        this.addView(child);
        this.bringChildToFront(child);
        final int parentWidth = this.getMeasuredWidth();
        final int parentHeight = this.getMeasuredHeight();
        final int parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
        final int parentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.EXACTLY);
        this.measureChildWithMargins(child, parentWidthMeasureSpec, 0, parentHeightMeasureSpec, 0);
        this.onViewAttachedToWindow(result);
        return result;
    }

    @Nullable
    private ExpandableRecyclerView.ViewHolder findFirstVisibleViewHolder(@NonNull ExpandableRecyclerView target) {
        final ExpandableRecyclerView.LayoutManager lm = target.getLayoutManager();
        if (lm == null) {
            return null;
        }
        for (int i = 0; i < lm.getChildCount(); i++) {
            final View child = lm.getChildAt(i);
            if (child == null) {
                continue;
            }
            if (lm.getDecoratedBottom(child) >= 0) {
                return (ExpandableRecyclerView.ViewHolder) target.getChildViewHolder(child);
            }
        }
        return null;
    }

    @Nullable
    private ExpandableRecyclerView.ViewHolder findFirstVisiblePreViewHolder(@NonNull ExpandableRecyclerView target,
                                                                            @NonNull ExpandableRecyclerView.ViewHolder holder) {
        final ExpandableRecyclerView.LayoutManager lm = target.getLayoutManager();
        if (lm == null) {
            return null;
        }
        final int groupPosition = getGroupPosition(holder);
        if (ExpandableRecyclerView.NO_POSITION == groupPosition) {
            return null;
        }
        int midIndex;
        int leftIndex = 0;
        int rightIndex = lm.getChildCount() - 1;

        while (leftIndex <= rightIndex) {
            midIndex = (leftIndex + rightIndex) / 2;

            final View preChild = lm.getChildAt(midIndex);
            if (preChild == null) {
                return null;
            }
            final ExpandableRecyclerView.ViewHolder preHolder;
            preHolder = (ExpandableRecyclerView.ViewHolder) target.getChildViewHolder(preChild);

            final int prePositionType = getPositionType(preHolder);
            if (PositionType.TYPE_NONE == prePositionType) {
                return null;
            }
            final int preGroupPosition = getGroupPosition(preHolder);
            if (ExpandableRecyclerView.NO_POSITION == preGroupPosition) {
                return null;
            }

            if (preGroupPosition < (groupPosition + 1)) {
                leftIndex = midIndex + 1;
            } else if (preGroupPosition > (groupPosition + 1)) {
                rightIndex = midIndex - 1;
            } else if (PositionType.TYPE_CHILD == prePositionType) {
                rightIndex = midIndex - 1;
            } else {
                return preHolder;
            }
        }
        return null;
    }

    private static int getPositionType(@Nullable ExpandableRecyclerView.ViewHolder holder) {
        if (holder == null) {
            return PositionType.TYPE_NONE;
        }
        final int positionType = holder.getPositionType();
        if (PositionType.TYPE_GROUP == positionType
                || PositionType.TYPE_CHILD == positionType) {
            return positionType;
        }
        return PositionType.TYPE_NONE;
    }

    private static int getItemViewType(@Nullable ExpandableRecyclerView.ViewHolder holder) {
        if (holder == null) {
            return ExpandableRecyclerView.INVALID_TYPE;
        }
        return holder.getItemViewType();
    }

    private static int getGroupPosition(@Nullable ExpandableRecyclerView.ViewHolder holder) {
        if (holder == null) {
            return ExpandableRecyclerView.NO_POSITION;
        }
        final int positionType = getPositionType(holder);
        if (PositionType.TYPE_NONE == positionType) {
            return ExpandableRecyclerView.NO_POSITION;
        }
        return holder.getGroupPosition();
    }

    private static class Component extends ExpandableRecyclerView.OnScrollListener implements
            ExpandableRecyclerView.OnChildAttachStateChangeListener,
            ExpandableRecyclerView.OnAdapterChangedListener,
            Runnable {
        @NonNull
        private final StickyRecyclerLayout mStickyRecyclerLayout;
        @Nullable
        private ExpandableRecyclerView mExpandableRecyclerView;
        @Nullable
        private AdapterDataObserver mAdapterDataObserver;

        private Component(@NonNull StickyRecyclerLayout stickyRecyclerLayout) {
            this.mStickyRecyclerLayout = stickyRecyclerLayout;
        }

        public void bind(@NonNull ExpandableRecyclerView expandableRecyclerView) {
            this.mExpandableRecyclerView = expandableRecyclerView;
            this.mExpandableRecyclerView.addOnScrollListener(this);
            this.mExpandableRecyclerView.addOnAdapterChangedListener(this);
            this.mExpandableRecyclerView.addOnChildAttachStateChangeListener(this);
        }

        public void unbind(@NonNull ExpandableRecyclerView expandableRecyclerView) {
            expandableRecyclerView.removeOnChildAttachStateChangeListener(this);
            expandableRecyclerView.removeOnAdapterChangedListener(this);
            expandableRecyclerView.removeOnScrollListener(this);
            this.mExpandableRecyclerView = null;
        }

        @Override
        public void run() {
            synchronized (StickyRecyclerLayout.class) {
                this.dispatchFloating();
            }
        }

        @Override
        public void onChildViewAttachedToWindow(@NonNull View itemView) {
            this.postOnFloating();
        }

        @Override
        public void onChildViewDetachedFromWindow(@NonNull View itemView) {
            this.postOnFloating();
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            this.dispatchFloating();
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
            this.dispatchFloating();
        }

        @Override
        public void onAdapterChanged(@NonNull RecyclerView recyclerView,
                                     @Nullable RecyclerView.Adapter<?> oldAdapter,
                                     @Nullable RecyclerView.Adapter<?> newAdapter) {
            if (oldAdapter != null) {
                if (this.mAdapterDataObserver != null) {
                    oldAdapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
                }
            }
            this.clearAndRecycleViewHolder();

            if (newAdapter != null) {
                this.dispatchFloating();

                if (this.mAdapterDataObserver == null) {
                    this.mAdapterDataObserver = new AdapterDataObserver(this);
                }
                newAdapter.registerAdapterDataObserver(this.mAdapterDataObserver);
            }
        }

        private void postOnFloating() {
            this.mStickyRecyclerLayout.removeCallbacks(this);
            ViewCompat.postOnAnimation(this.mStickyRecyclerLayout, this);
        }

        private void dispatchFloating() {
            final ExpandableRecyclerView expandableRecyclerView = this.mExpandableRecyclerView;
            if (expandableRecyclerView != null) {
                this.mStickyRecyclerLayout.removeCallbacks(this);
                this.mStickyRecyclerLayout.dispatchFloating(expandableRecyclerView);
            }
        }

        private void dispatchDataSetChanged() {
            final ExpandableRecyclerView expandableRecyclerView = this.mExpandableRecyclerView;
            if (expandableRecyclerView != null) {
                this.mStickyRecyclerLayout.dispatchDataSetChanged(expandableRecyclerView);
            }
        }

        private void clearAndRecycleViewHolder() {
            final ExpandableRecyclerView expandableRecyclerView = this.mExpandableRecyclerView;
            if (expandableRecyclerView != null) {
                this.mStickyRecyclerLayout.clearAndRecycleViewHolder(expandableRecyclerView);
            }
        }
    }

    private static class AdapterDataObserver extends ExpandableRecyclerView.AdapterDataObserver {
        @NonNull
        private final Component mComponent;

        private AdapterDataObserver(@NonNull Component component) {
            this.mComponent = component;
        }

        @Override
        public void onChanged() {
            this.mComponent.dispatchDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.mComponent.dispatchDataSetChanged();
        }
    }
}
