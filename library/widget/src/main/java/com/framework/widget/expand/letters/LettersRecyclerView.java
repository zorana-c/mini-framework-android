package com.framework.widget.expand.letters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.framework.widget.expand.ExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2022/5/23
 * @Email : 171905184@qq.com
 * @Description : 快速索引
 */
public class LettersRecyclerView extends ExpandableRecyclerView {
    private final ArrayList<OnLettersChangedListener> listeners = new ArrayList<>();
    private final Runnable cancelTouchAction = () -> {
        this.dispatchLettersChanged(null);
    };

    @Nullable
    private ViewHolder holderLast;
    private long cancelTouchDelayMillis = 320L;

    public LettersRecyclerView(@NonNull Context context) {
        super(context);
    }

    public LettersRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LettersRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int parentWidthSpec, int parentHeightSpec) {
        super.onMeasure(parentWidthSpec, MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }

    @SuppressLint("WrongCall")
    public void superMeasure(int parentWidthSpec, int parentHeightSpec) {
        super.onMeasure(parentWidthSpec, parentHeightSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        return true;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        this.requestParentDisallowInterceptTouchEvent();
        final ViewHolder holderLast = this.holderLast;
        final int x = (int) (event.getX() + 0.5f);
        final int y = (int) (event.getY() + 0.5f);

        final int actionMasked = event.getActionMasked();
        if (MotionEvent.ACTION_DOWN == actionMasked) {
            this.removeCallbacks(this.cancelTouchAction);
            this.holderLast = null;
        }

        final ViewHolder scrolling;
        if (this.pointViewHolderUnder(holderLast, x, y)) {
            scrolling = holderLast;
        } else {
            scrolling = this.findViewHolderUnder(x, y);
        }
        if (this.canStartLettersScrolling(scrolling)) {
            this.removeCallbacks(this.cancelTouchAction);
        } else {
            this.execCancelTouchAction();
            return true;
        }

        if (MotionEvent.ACTION_UP == actionMasked ||
                MotionEvent.ACTION_CANCEL == actionMasked) {
            this.execCancelTouchAction();
        } else {
            final boolean change;
            if (holderLast == null) {
                change = true;
            } else if (holderLast.getPositionType() != scrolling.getPositionType()) {
                change = true;
            } else change = holderLast.getGroupPosition() != scrolling.getGroupPosition();
            if (change) {
                this.dispatchLettersChanged(scrolling);
                this.scrollToTarget(scrolling);
            }
            this.dispatchLettersScrolled(scrolling);
            if (this.holderLast != scrolling) {
                this.holderLast = scrolling;
            }
        }
        return true;
    }

    private void execCancelTouchAction() {
        if (this.holderLast != null) {
            this.holderLast = null;
            this.removeCallbacks(this.cancelTouchAction);
            this.postOnAnimationDelayed(this.cancelTouchAction, this.cancelTouchDelayMillis);
        }
    }

    private boolean canStartLettersScrolling(@Nullable ViewHolder holder) {
        if (holder == null) {
            return false;
        }
        final Adapter<?> adapter = holder.getAdapter();
        if (adapter == null) {
            return false;
        }
        final ExpandableRecyclerView.Adapter<?> targetAdapter = adapter.getTargetAdapter();

        if (targetAdapter == null) {
            return false;
        }
        final ExpandableRecyclerView targetRecyclerView = targetAdapter.getRecyclerView();
        return targetRecyclerView != null
                && targetRecyclerView.getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING;
    }

    @Nullable
    private ViewHolder findViewHolderUnder(int x, int y) {
        final int N = this.getChildCount();
        for (int index = N - 1; index >= 0; index--) {
            final View child = this.getChildAt(index);
            final RecyclerView.ViewHolder holder = this.getChildViewHolder(child);
            if (!(holder instanceof ViewHolder)) {
                break;
            }
            final ViewHolder expHolder = (ViewHolder) holder;
            if (this.pointViewHolderUnder(expHolder, x, y)) {
                return expHolder;
            }
        }
        return null;
    }

    private boolean pointViewHolderUnder(@Nullable ViewHolder holder, int x, int y) {
        final View child = holder == null ? null : holder.itemView;
        return child != null
                && y >= child.getTop() + child.getTranslationY()
                && y < child.getBottom() + child.getTranslationY();
    }

    private void requestParentDisallowInterceptTouchEvent() {
        final ViewParent parent = this.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void onLettersScrolled(@NonNull ViewHolder holder) {
    }

    public void onLettersChanged(@Nullable ViewHolder holder) {
    }

    @Override
    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        if (adapter == null || adapter instanceof Adapter) {
            super.setAdapter(adapter);
        } else {
            throw new IllegalStateException("ERROR");
        }
    }

    @Override
    public void swapAdapter(@Nullable RecyclerView.Adapter adapter,
                            boolean removeAndRecycleExistViews) {
        if (adapter == null || adapter instanceof Adapter) {
            super.swapAdapter(adapter, removeAndRecycleExistViews);
        } else {
            throw new IllegalStateException("ERROR");
        }
    }

    public long getCancelTouchDelayMillis() {
        return this.cancelTouchDelayMillis;
    }

    public void setCancelTouchDelayMillis(long delayMillis) {
        this.cancelTouchDelayMillis = delayMillis;
    }

    public void addOnLettersChangedListener(@NonNull OnLettersChangedListener listener) {
        this.listeners.add(listener);
    }

    public void removeOnLettersChangedListener(@NonNull OnLettersChangedListener listener) {
        this.listeners.remove(listener);
    }

    protected void scrollToPosition(@NonNull ExpandableRecyclerView target, int position) {
        final RecyclerView.LayoutManager layoutManager = target.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
            lm.scrollToPositionWithOffset(position, 0);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) layoutManager;
            lm.scrollToPositionWithOffset(position, 0);
        } else {
            target.scrollToPosition(position);
        }
    }

    private void scrollToTarget(@NonNull ViewHolder holder) {
        final Adapter<?> adapter = holder.getAdapter();
        if (adapter == null) {
            return;
        }
        final ExpandableRecyclerView.Adapter<?> targetAdapter = adapter.getTargetAdapter();
        if (targetAdapter == null) {
            return;
        }
        final ExpandableRecyclerView targetRecyclerView = targetAdapter.getRecyclerView();
        if (targetRecyclerView == null) {
            return;
        }
        final int targetScrollState = targetRecyclerView.getScrollState();
        if (targetScrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
            return;
        }
        final int position = targetAdapter.getAdapterPositionByPositionType(
                holder.getPositionType(),
                holder.getGroupPosition());
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        targetRecyclerView.stopScroll();
        this.scrollToPosition(targetRecyclerView, position);
    }

    private void dispatchLettersScrolled(@NonNull ViewHolder holder) {
        this.onLettersScrolled(holder);

        for (OnLettersChangedListener listener : this.listeners) {
            listener.onLettersScrolled(this, holder);
        }
    }

    private void dispatchLettersChanged(@Nullable ViewHolder holder) {
        this.onLettersChanged(holder);

        for (OnLettersChangedListener listener : this.listeners) {
            listener.onLettersChanged(this, holder);
        }
    }

    public static abstract class ViewHolder extends ExpandableRecyclerView.ViewHolder {
        /* Item select state*/
        boolean hasSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * 是否选中: 滑动当前位置
         */
        public final boolean hasSelected() {
            return this.hasSelected;
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> extends ExpandableRecyclerView.Adapter<VH> {
        private int positionLast = RecyclerView.NO_POSITION;
        private OnScrollListener onScrollListener;
        private ExpandableRecyclerView expandableRecyclerView;
        private OnAdapterChangedListener onAdapterChangedListener;

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager == null) {
                layoutManager = new LinearLayoutManager(recyclerView.getContext());
            }
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            try {
                holder.hasSelected = (this.positionLast == position);
            } finally {
                super.onBindViewHolder(holder, position, payloads);
            }
        }

        @NonNull
        @Override
        public VH onCreateHeadViewHolder(@NonNull ViewGroup parent, int headItemViewType) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @NonNull
        @Override
        public VH onCreateTailViewHolder(@NonNull ViewGroup parent, int tailItemViewType) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @NonNull
        @Override
        public VH onCreateEmptyViewHolder(@NonNull ViewGroup parent, int emptyItemViewType) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @NonNull
        @Override
        public VH onCreateChildViewHolder(@NonNull ViewGroup parent, int childItemViewType) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @Override
        public void onBindHeadViewHolder(@NonNull VH holder, int position) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @Override
        public void onBindTailViewHolder(@NonNull VH holder, int position) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @Override
        public void onBindEmptyViewHolder(@NonNull VH holder, int position) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @Override
        public void onBindChildViewHolder(@NonNull VH holder, int groupPosition, int childPosition) {
            throw new RuntimeException("OVERRIDE IT");
        }

        @Nullable
        @Override
        public CharSequence getHeadItemLetter(int position) {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getHeadItemLetter(position);
            }
            return super.getHeadItemLetter(position);
        }

        @Nullable
        @Override
        public CharSequence getTailItemLetter(int position) {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getTailItemLetter(position);
            }
            return super.getTailItemLetter(position);
        }

        @Nullable
        @Override
        public CharSequence getEmptyItemLetter(int position) {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getEmptyItemLetter(position);
            }
            return super.getEmptyItemLetter(position);
        }

        @Nullable
        @Override
        public CharSequence getGroupItemLetter(int groupPosition) {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getGroupItemLetter(groupPosition);
            }
            return super.getGroupItemLetter(groupPosition);
        }

        @Nullable
        @Override
        public CharSequence getChildItemLetter(int groupPosition, int childPosition) {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getChildItemLetter(groupPosition, childPosition);
            }
            return super.getChildItemLetter(groupPosition, childPosition);
        }

        @Override
        public int getHeadItemCount() {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getHeadItemCount();
            }
            return super.getHeadItemCount();
        }

        @Override
        public int getTailItemCount() {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getTailItemCount();
            }
            return super.getTailItemCount();
        }

        @Override
        public int getEmptyItemCount() {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getEmptyItemCount();
            }
            return super.getEmptyItemCount();
        }

        @Override
        public int getGroupItemCount() {
            final ExpandableRecyclerView.Adapter<?> adapter = this.getTargetAdapter();
            if (adapter != null) {
                return adapter.getGroupItemCount();
            }
            return 0;
        }

        @Override
        public int getChildItemCount(int groupPosition) {
            // not override it.
            return 0;
        }

        public void onLettersChanged(@NonNull ViewHolder targetHolder) {
        }

        public final int positionLast() {
            return this.positionLast;
        }

        @Nullable
        public final ExpandableRecyclerView getTarget() {
            return this.expandableRecyclerView;
        }

        @Nullable
        public final ExpandableRecyclerView.Adapter<?> getTargetAdapter() {
            final ExpandableRecyclerView target = this.getTarget();
            if (target == null) {
                return null;
            }
            return (ExpandableRecyclerView.Adapter<?>) target.getAdapter();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void attachedToTarget(@Nullable ExpandableRecyclerView target) {
            final ExpandableRecyclerView oldTarget = this.expandableRecyclerView;
            if (oldTarget == target) {
                return;
            }
            if (oldTarget != null) {
                oldTarget.removeOnScrollListener(this.onScrollListener);
                oldTarget.removeOnAdapterChangedListener(this.onAdapterChangedListener);
            }
            this.expandableRecyclerView = target;
            if (target != null) {
                if (this.onScrollListener == null) {
                    this.onScrollListener = new OnScrollListener(this);
                }
                if (this.onAdapterChangedListener == null) {
                    this.onAdapterChangedListener = new OnAdapterChangedListener(this);
                }
                target.addOnScrollListener(this.onScrollListener);
                target.addOnAdapterChangedListener(this.onAdapterChangedListener);

                final ExpandableRecyclerView.Adapter<?> targetAdapter;
                targetAdapter = (ExpandableRecyclerView.Adapter<?>) target.getAdapter();
                if (targetAdapter != null) {
                    targetAdapter.notifyDataSetChanged();
                    return;
                }
            }
            this.notifyDataSetChanged();
        }

        private void dispatchLettersChanged(@NonNull ViewHolder targetHolder) {
            final int positionLast;
            synchronized (this) {
                positionLast = this.positionLast;
                this.positionLast = this.getAdapterPositionByPositionType(
                        targetHolder.getPositionType(),
                        targetHolder.getGroupPosition());
            }
            final RecyclerView recyclerView = this.requireRecyclerView();
            final VH oldHolder = (VH) recyclerView.findViewHolderForLayoutPosition(positionLast);
            final VH newHolder = (VH) recyclerView.findViewHolderForLayoutPosition(this.positionLast);
            if (oldHolder != null) {
                this.bindViewHolder(oldHolder, positionLast);
            }
            if (newHolder != null) {
                this.bindViewHolder(newHolder, this.positionLast);
            }
            this.onLettersChanged(targetHolder);
        }

        private static class OnScrollListener extends RecyclerView.OnScrollListener {
            private final Runnable lettersChangedExec = this::dispatchLettersChanged;
            private final Adapter<?> adapter;
            private ViewHolder holderLast;

            private OnScrollListener(@NonNull Adapter<?> adapter) {
                this.adapter = adapter;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                this.smoothScrollToLetters(recyclerView);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
                this.smoothScrollToLetters(recyclerView);
            }

            private void smoothScrollToLetters(@NonNull RecyclerView recyclerView) {
                final ViewHolder holder = this.blockingFirst(recyclerView);
                final ViewHolder holderLast = this.holderLast;
                if (holder == null) {
                    return;
                }
                final boolean change;
                if (holderLast == null) {
                    change = true;
                } else if (holderLast.getPositionType() != holder.getPositionType()) {
                    change = true;
                } else change = holderLast.getGroupPosition() != holder.getGroupPosition();
                if (change) {
                    this.holderLast = holder;
                    recyclerView.removeCallbacks(this.lettersChangedExec);
                    recyclerView.postOnAnimation(this.lettersChangedExec);
                }
            }

            @Nullable
            private ViewHolder blockingFirst(@NonNull RecyclerView recyclerView) {
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null
                        || layoutManager.getChildCount() == 0) {
                    return null;
                }
                final View child = layoutManager.getChildAt(0);
                if (child == null) {
                    return null;
                }
                final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(child);
                if (holder instanceof ViewHolder) {
                    return (ViewHolder) holder;
                }
                return null;
            }

            private void dispatchLettersChanged() {
                final ViewHolder holder;
                synchronized (this) {
                    holder = this.holderLast;
                }
                if (holder != null) {
                    this.adapter.dispatchLettersChanged(holder);
                }
            }
        }

        private static class OnAdapterChangedListener implements ExpandableRecyclerView.OnAdapterChangedListener {
            private final AdapterDataObserver observer;

            public OnAdapterChangedListener(@NonNull Adapter<?> adapter) {
                this.observer = new AdapterDataObserver(adapter);
            }

            @Override
            public void onAdapterChanged(@NonNull RecyclerView recyclerView,
                                         @Nullable RecyclerView.Adapter<?> oldAdapter,
                                         @Nullable RecyclerView.Adapter<?> newAdapter) {
                if (oldAdapter != null) {
                    oldAdapter.unregisterAdapterDataObserver(this.observer);
                }
                if (newAdapter != null) {
                    newAdapter.registerAdapterDataObserver(this.observer);
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            private static class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
                private final Adapter<?> adapter;

                private AdapterDataObserver(@NonNull Adapter<?> adapter) {
                    this.adapter = adapter;
                }

                @Override
                public void onChanged() {
                    this.adapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                    this.adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public interface OnLettersChangedListener {

        void onLettersChanged(@NonNull LettersRecyclerView parent, @Nullable ViewHolder holder);

        void onLettersScrolled(@NonNull LettersRecyclerView parent, @NonNull ViewHolder holder);
    }

    public static abstract class SimpleOnLettersChangedListener implements OnLettersChangedListener {
        @Override
        public void onLettersChanged(@NonNull LettersRecyclerView parent, @Nullable ViewHolder holder) {
        }

        @Override
        public void onLettersScrolled(@NonNull LettersRecyclerView parent, @NonNull ViewHolder holder) {
        }
    }
}
