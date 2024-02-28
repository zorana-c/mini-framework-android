package com.framework.widget.expand;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.view.AbsSavedState;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.widget.sliver.SliverRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2023-08-18
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ExpandableRecyclerView extends SliverRecyclerView {
    @NonNull
    private final ArrayList<OnGroupExpandListener> mOnGroupExpandListeners = new ArrayList<>();
    @NonNull
    private final ArrayList<OnGroupCollapseListener> mOnGroupCollapseListeners = new ArrayList<>();
    @NonNull
    private final ArrayList<OnAdapterChangedListener> mOnAdapterChangedListeners = new ArrayList<>();

    @Nullable
    private OnItemClickListener<ViewHolder> mOnHeadItemClickListener;
    @Nullable
    private OnItemClickListener<ViewHolder> mOnTailItemClickListener;
    @Nullable
    private OnItemClickListener<ViewHolder> mOnEmptyItemClickListener;
    @Nullable
    private OnItemClickListener<ViewHolder> mOnGroupItemClickListener;
    @Nullable
    private OnChildItemClickListener<ViewHolder> mOnChildItemClickListener;

    @Nullable
    private OnItemLongClickListener<ViewHolder> mOnHeadItemLongClickListener;
    @Nullable
    private OnItemLongClickListener<ViewHolder> mOnTailItemLongClickListener;
    @Nullable
    private OnItemLongClickListener<ViewHolder> mOnEmptyItemLongClickListener;
    @Nullable
    private OnItemLongClickListener<ViewHolder> mOnGroupItemLongClickListener;
    @Nullable
    private OnChildItemLongClickListener<ViewHolder> mOnChildItemLongClickListener;

    public ExpandableRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ExpandableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter != null) {
            savedState.mAdapterState = adapter.onSaveInstanceState();
        }
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
            if (adapter != null) {
                adapter.onRestoreInstanceState(savedState.mAdapterState);
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @NonNull
    public final <T extends LayoutManager> T requireLayoutManager() {
        final LayoutManager layoutManager = this.getLayoutManager();
        if (layoutManager == null) {
            throw new NullPointerException("ERROR");
        }
        return (T) layoutManager;
    }

    @Override
    @CallSuper
    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        final RecyclerView.Adapter<?> oldAdapter = this.getAdapter();
        super.setAdapter(adapter);
        this.dispatchOnAdapterChanged(oldAdapter, adapter);
    }

    @Override
    @CallSuper
    public void swapAdapter(@Nullable RecyclerView.Adapter adapter,
                            boolean removeAndRecycleExistViews) {
        final RecyclerView.Adapter<?> oldAdapter = this.getAdapter();
        super.swapAdapter(adapter, removeAndRecycleExistViews);
        this.dispatchOnAdapterChanged(oldAdapter, adapter);
    }

    @NonNull
    public final <T extends RecyclerView.Adapter<?>> T requireAdapter() {
        final RecyclerView.Adapter<?> adapter = this.getAdapter();
        if (adapter == null) {
            throw new NullPointerException("ERROR");
        }
        return (T) adapter;
    }

    public int getMaxExpandItemCount() {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return -1;
        }
        return adapter.getMaxExpandItemCount();
    }

    public void setMaxExpandItemCount(int maxExpandItemCount) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter != null) {
            adapter.setMaxExpandItemCount(maxExpandItemCount);
        }
    }

    public boolean expandGroup(int groupPosition) {
        return this.expandGroup(groupPosition, true);
    }

    public boolean expandGroup(int groupPosition, boolean animate) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return false;
        }
        if (adapter.expandGroup(groupPosition)) {
            if (animate) {
                this.smoothScrollToGroupPosition(groupPosition);
            }
            return true;
        }
        return false;
    }

    public boolean collapseGroup(int groupPosition) {
        return this.collapseGroup(groupPosition, true);
    }

    public boolean collapseGroup(int groupPosition, boolean animate) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return false;
        }
        if (adapter.collapseGroup(groupPosition)) {
            if (animate) {
                this.smoothScrollToGroupPosition(groupPosition);
            }
            return true;
        }
        return false;
    }

    public boolean isGroupExpanded(int groupPosition) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isGroupExpanded(groupPosition);
    }

    public void scrollToGroupPosition(int groupPosition) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return;
        }
        final int position;
        position = adapter.getAdapterPositionByPositionType(PositionType.TYPE_GROUP, groupPosition);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        this.scrollToPosition(position);
    }

    public void scrollToChildPosition(int groupPosition, int childPosition) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return;
        }
        final int position;
        position = adapter.getChildAdapterPosition(groupPosition, childPosition);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        this.scrollToPosition(position);
    }

    public void smoothScrollToGroupPosition(int groupPosition) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return;
        }
        final int position;
        position = adapter.getAdapterPositionByPositionType(PositionType.TYPE_GROUP, groupPosition);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        this.smoothScrollToPosition(position);
    }

    public void smoothScrollToChildPosition(int groupPosition, int childPosition) {
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter == null) {
            return;
        }
        final int position;
        position = adapter.getChildAdapterPosition(groupPosition, childPosition);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        this.smoothScrollToPosition(position);
    }

    public void setOnHeadItemClickListener(@Nullable OnItemClickListener<?> listener) {
        this.mOnHeadItemClickListener = (OnItemClickListener<ViewHolder>) listener;
    }

    public void setOnTailItemClickListener(@Nullable OnItemClickListener<?> listener) {
        this.mOnTailItemClickListener = (OnItemClickListener<ViewHolder>) listener;
    }

    public void setOnEmptyItemClickListener(@Nullable OnItemClickListener<?> listener) {
        this.mOnEmptyItemClickListener = (OnItemClickListener<ViewHolder>) listener;
    }

    public void setOnGroupItemClickListener(@Nullable OnItemClickListener<?> listener) {
        this.mOnGroupItemClickListener = (OnItemClickListener<ViewHolder>) listener;
    }

    public void setOnChildItemClickListener(@Nullable OnChildItemClickListener<?> listener) {
        this.mOnChildItemClickListener = (OnChildItemClickListener<ViewHolder>) listener;
    }

    public void setOnHeadItemLongClickListener(@Nullable OnItemLongClickListener<?> listener) {
        this.mOnHeadItemLongClickListener = (OnItemLongClickListener<ViewHolder>) listener;
    }

    public void setOnTailItemLongClickListener(@Nullable OnItemLongClickListener<?> listener) {
        this.mOnTailItemLongClickListener = (OnItemLongClickListener<ViewHolder>) listener;
    }

    public void setOnEmptyItemLongClickListener(@Nullable OnItemLongClickListener<?> listener) {
        this.mOnEmptyItemLongClickListener = (OnItemLongClickListener<ViewHolder>) listener;
    }

    public void setOnGroupItemLongClickListener(@Nullable OnItemLongClickListener<?> listener) {
        this.mOnGroupItemLongClickListener = (OnItemLongClickListener<ViewHolder>) listener;
    }

    public void setOnChildItemLongClickListener(@Nullable OnChildItemLongClickListener<?> listener) {
        this.mOnChildItemLongClickListener = (OnChildItemLongClickListener<ViewHolder>) listener;
    }

    public void addOnGroupExpandListener(@NonNull OnGroupExpandListener listener) {
        this.mOnGroupExpandListeners.add(listener);
    }

    public void removeOnGroupExpandListener(@NonNull OnGroupExpandListener listener) {
        this.mOnGroupExpandListeners.remove(listener);
    }

    public void addOnGroupCollapseListener(@NonNull OnGroupCollapseListener listener) {
        this.mOnGroupCollapseListeners.add(listener);
    }

    public void removeOnGroupCollapseListener(@NonNull OnGroupCollapseListener listener) {
        this.mOnGroupCollapseListeners.remove(listener);
    }

    public void addOnAdapterChangedListener(@NonNull OnAdapterChangedListener listener) {
        this.mOnAdapterChangedListeners.add(listener);
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter != null) {
            listener.onAdapterChanged(this, null, adapter);
        }
    }

    public void removeOnAdapterChangedListener(@NonNull OnAdapterChangedListener listener) {
        this.mOnAdapterChangedListeners.remove(listener);
        final Adapter<?> adapter = (Adapter<?>) this.getAdapter();
        if (adapter != null) {
            listener.onAdapterChanged(this, adapter, null);
        }
    }

    private void dispatchOnAdapterChanged(@Nullable RecyclerView.Adapter<?> oldAdapter,
                                          @Nullable RecyclerView.Adapter<?> newAdapter) {
        for (OnAdapterChangedListener listener : this.mOnAdapterChangedListeners) {
            listener.onAdapterChanged(this, oldAdapter, newAdapter);
        }
    }

    private void dispatchOnGroupExpand(int groupPosition) {
        for (OnGroupExpandListener listener : this.mOnGroupExpandListeners) {
            listener.onGroupExpand(this, groupPosition);
        }
    }

    private void dispatchOnGroupCollapse(int groupPosition) {
        for (OnGroupCollapseListener listener : this.mOnGroupCollapseListeners) {
            listener.onGroupCollapse(this, groupPosition);
        }
    }

    @SuppressLint("SwitchIntDef")
    private void dispatchOnItemClick(@NonNull ViewHolder holder, @NonNull View target) {
        final int positionType = holder.getPositionType();
        final int groupPosition = holder.getGroupPosition();
        final int childPosition = holder.getChildPosition();

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                if (this.mOnHeadItemClickListener != null) {
                    this.mOnHeadItemClickListener.onItemClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_TAIL:
                if (this.mOnTailItemClickListener != null) {
                    this.mOnTailItemClickListener.onItemClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_EMPTY:
                if (this.mOnEmptyItemClickListener != null) {
                    this.mOnEmptyItemClickListener.onItemClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_GROUP:
                if (this.mOnGroupItemClickListener != null) {
                    this.mOnGroupItemClickListener.onItemClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_CHILD:
                if (this.mOnChildItemClickListener != null) {
                    this.mOnChildItemClickListener.onChildItemClick(holder, target, groupPosition, childPosition);
                }
                break;
        }
    }

    @SuppressLint("SwitchIntDef")
    private boolean dispatchOnItemLongClick(@NonNull ViewHolder holder, @NonNull View target) {
        final int positionType = holder.getPositionType();
        final int groupPosition = holder.getGroupPosition();
        final int childPosition = holder.getChildPosition();
        boolean handled = false;

        switch (positionType) {
            case PositionType.TYPE_HEAD:
                if (this.mOnHeadItemLongClickListener != null) {
                    handled = this.mOnHeadItemLongClickListener.onItemLongClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_TAIL:
                if (this.mOnTailItemLongClickListener != null) {
                    handled = this.mOnTailItemLongClickListener.onItemLongClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_EMPTY:
                if (this.mOnEmptyItemLongClickListener != null) {
                    handled = this.mOnEmptyItemLongClickListener.onItemLongClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_GROUP:
                if (this.mOnGroupItemLongClickListener != null) {
                    handled = this.mOnGroupItemLongClickListener.onItemLongClick(holder, target, groupPosition);
                }
                break;
            case PositionType.TYPE_CHILD:
                if (this.mOnChildItemLongClickListener != null) {
                    handled = this.mOnChildItemLongClickListener.onChildItemLongClick(holder, target, groupPosition, childPosition);
                }
                break;
        }
        return handled;
    }

    public static abstract class ViewHolder extends SliverRecyclerView.ViewHolder {
        @Nullable
        private List<Object> mPayloads;
        @Nullable
        private RecyclerView mRecyclerView;
        @Nullable
        private ComponentListener mComponentListener;

        @PositionType
        int mPositionType;
        int mItemViewType;
        int mGroupPosition;
        int mChildPosition;
        boolean mItemExpanded;
        @Nullable
        CharSequence mItemLetter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.setOnClickListener(itemView);
            this.setOnLongClickListener(itemView);
        }

        @CallSuper
        public void onRecycled() {
            // nothing
        }

        @CallSuper
        public void onAttachedToWindow() {
            // nothing
        }

        @CallSuper
        public void onDetachedFromWindow() {
            // nothing
        }

        @PositionType
        public final int getPositionType() {
            return this.mPositionType;
        }

        public final int getGroupPosition() {
            return this.mGroupPosition;
        }

        public final int getChildPosition() {
            return this.mChildPosition;
        }

        public final int getRetItemViewType() {
            return this.mItemViewType;
        }

        public final boolean getItemExpanded() {
            return this.mItemExpanded;
        }

        @NonNull
        public final List<Object> getPayloads() {
            if (this.mPayloads == null) {
                return Collections.emptyList();
            }
            return this.mPayloads;
        }

        @Nullable
        public final CharSequence getItemLetter() {
            return this.mItemLetter;
        }

        @NonNull
        public final CharSequence requireItemLetter() {
            final CharSequence itemLetter = this.getItemLetter();
            if (itemLetter == null) {
                throw new NullPointerException("ERROR");
            }
            return itemLetter;
        }

        @Nullable
        public final <R extends RecyclerView> R getRecyclerView() {
            return (R) this.mRecyclerView;
        }

        @NonNull
        public final <R extends RecyclerView> R requireRecyclerView() {
            final R recyclerView = this.getRecyclerView();
            if (recyclerView == null) {
                throw new NullPointerException("ERROR");
            }
            return recyclerView;
        }

        @Nullable
        public final <R extends Adapter<?>> R getAdapter() {
            final RecyclerView recyclerView = this.getRecyclerView();
            if (recyclerView == null) {
                return null;
            }
            return (R) recyclerView.getAdapter();
        }

        @NonNull
        public final <R extends Adapter<?>> R requireAdapter() {
            final R adapter = this.getAdapter();
            if (adapter == null) {
                throw new NullPointerException("ERROR");
            }
            return adapter;
        }

        public final void notifyItemChanged() {
            this.notifyItemChanged(null);
        }

        public final void notifyItemChanged(@Nullable Object payload) {
            final int layoutPosition = this.getLayoutPosition();
            if (layoutPosition == RecyclerView.NO_POSITION) {
                return;
            }
            final Adapter<?> adapter = this.getAdapter();
            if (adapter != null) {
                adapter.notifyItemChanged(layoutPosition, payload);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        public final void notifyDataSetChanged() {
            final Adapter<?> adapter = this.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Nullable
        public final <T extends View> T findViewById(@IdRes int id) {
            return this.itemView.findViewById(id);
        }

        @NonNull
        public final <T extends View> T requireViewById(@IdRes int id) {
            final T view = this.findViewById(id);
            if (view == null) {
                throw new NullPointerException("ERROR");
            }
            return view;
        }

        public final void setOnClickListener(@IdRes int id) {
            this.setOnClickListener(this.requireViewById(id));
        }

        public final void setOnClickListener(@NonNull View target) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            target.setOnClickListener(this.mComponentListener);
        }

        public final void setOnLongClickListener(@IdRes int id) {
            this.setOnLongClickListener(this.requireViewById(id));
        }

        public final void setOnLongClickListener(@NonNull View target) {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            target.setOnLongClickListener(this.mComponentListener);
        }

        final void addChangePayloads(@NonNull List<Object> payloads) {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList<>();
            }
            this.mPayloads.addAll(payloads);
        }

        final void setRecyclerView(@Nullable RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
        }

        final void clearPayloads() {
            if (this.mPayloads != null) {
                this.mPayloads.clear();
            }
        }

        final void recycler() {
            this.clearPayloads();
            this.mRecyclerView = null;
            this.mComponentListener = null;
            this.mItemLetter = null;
            this.mItemExpanded = false;
            this.mGroupPosition = RecyclerView.NO_POSITION;
            this.mChildPosition = RecyclerView.NO_POSITION;
        }

        private void dispatchOnClick(@NonNull View target) {
            final ExpandableRecyclerView recyclerView;
            recyclerView = this.getRecyclerView();
            if (recyclerView == null
                    || recyclerView.isAnimating()) {
                return;
            }
            recyclerView.dispatchOnItemClick(this, target);
        }

        private boolean dispatchOnLongClick(@NonNull View target) {
            final ExpandableRecyclerView recyclerView;
            recyclerView = this.getRecyclerView();
            if (recyclerView == null
                    || recyclerView.isAnimating()) {
                return false;
            }
            return recyclerView.dispatchOnItemLongClick(this, target);
        }

        private final class ComponentListener
                implements View.OnClickListener, View.OnLongClickListener {
            @Override
            public void onClick(@NonNull View target) {
                ViewHolder.this.dispatchOnClick(target);
            }

            @Override
            public boolean onLongClick(@NonNull View target) {
                return ViewHolder.this.dispatchOnLongClick(target);
            }
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> extends SliverRecyclerView.Adapter<VH>
            implements ExpandableAdapter<VH> {
        @NonNull
        private final ExpandableController<VH> mExpandableController;

        {
            this.mExpandableController = new ExpandableController<>(this);
        }

        @Override
        @CallSuper
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            if (!(recyclerView instanceof ExpandableRecyclerView)) {
                throw new IllegalStateException("ERROR");
            }
            this.mExpandableController.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        @CallSuper
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.mExpandableController.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        @CallSuper
        public void onViewRecycled(@NonNull VH holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();
            holder.recycler();
        }

        @Override
        @CallSuper
        public void onViewAttachedToWindow(@NonNull VH holder) {
            super.onViewAttachedToWindow(holder);
            holder.onAttachedToWindow();
        }

        @Override
        @CallSuper
        public void onViewDetachedFromWindow(@NonNull VH holder) {
            super.onViewDetachedFromWindow(holder);
            holder.onDetachedFromWindow();
        }

        @NonNull
        @Override
        @CallSuper
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int combinedItemType) {
            return this.mExpandableController.onCreateViewHolder(parent, combinedItemType);
        }

        @Override
        @CallSuper
        public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            this.mExpandableController.onBindViewHolder(holder, position, payloads);
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            // nothing
        }

        @Override
        @CallSuper
        public long getItemId(int position) {
            return this.mExpandableController.getItemId(position);
        }

        @Override
        @CallSuper
        public int getItemViewType(int position) {
            return this.mExpandableController.getItemViewType(position);
        }

        @Override
        @CallSuper
        public int getItemCount() {
            return this.mExpandableController.getItemCount();
        }

        @NonNull
        @CallSuper
        public Parcelable onSaveInstanceState() {
            return this.mExpandableController.onSaveInstanceState();
        }

        @CallSuper
        public void onRestoreInstanceState(@NonNull Parcelable state) {
            this.mExpandableController.onRestoreInstanceState(state);
        }

        @Nullable
        public final LayoutInflater getLayoutInflater() {
            return this.mExpandableController.getLayoutInflater();
        }

        @NonNull
        public final LayoutInflater requireLayoutInflater() {
            return this.mExpandableController.requireLayoutInflater();
        }

        @Nullable
        public final <V extends RecyclerView> V getRecyclerView() {
            return this.mExpandableController.getRecyclerView();
        }

        @NonNull
        public final <V extends RecyclerView> V requireRecyclerView() {
            return this.mExpandableController.requireRecyclerView();
        }

        @NonNull
        public final <V extends View> V inflate(@LayoutRes int layoutId) {
            return this.mExpandableController.inflate(layoutId);
        }

        @NonNull
        public final <V extends View> V inflate(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
            return this.mExpandableController.inflate(layoutId, parent);
        }

        // ExpandableAdapter

        @Override
        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            this.mExpandableController.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            this.mExpandableController.unregisterAdapterDataObserver(observer);
        }

        @NonNull
        public final PositionMetadata findFlattenedItemMetadata(int groupPosition) {
            return this.findFlattenedItemMetadata(PositionType.TYPE_GROUP, groupPosition);
        }

        @NonNull
        public final PositionMetadata findFlattenedItemMetadata(@PositionType int positionType, int groupPosition) {
            return this.mExpandableController.findFlattenedItemMetadata(positionType, groupPosition);
        }

        @NonNull
        public final PositionMetadata findUnFlattenedItemMetadata(int adapterPosition) {
            return this.mExpandableController.findUnFlattenedItemMetadata(adapterPosition);
        }

        @Override
        public final int getMaxExpandItemCount() {
            return this.mExpandableController.getMaxExpandItemCount();
        }

        @Override
        public final void setMaxExpandItemCount(int maxExpandItemCount) {
            this.mExpandableController.setMaxExpandItemCount(maxExpandItemCount);
        }

        @Override
        @PositionType
        public final int getPositionTypeByAdapterPosition(int adapterPosition) {
            return this.mExpandableController.getPositionTypeByAdapterPosition(adapterPosition);
        }

        @Override
        public final int getGroupPositionByAdapterPosition(int adapterPosition) {
            return this.mExpandableController.getGroupPositionByAdapterPosition(adapterPosition);
        }

        @Override
        public final int getAdapterPositionByPositionType(@PositionType int positionType, int groupPosition) {
            return this.mExpandableController.getAdapterPositionByPositionType(positionType, groupPosition);
        }

        @Override
        public final int getChildAdapterPosition(int groupPosition, int childPosition) {
            return this.mExpandableController.getChildAdapterPosition(groupPosition, childPosition);
        }

        @Override
        public final boolean expandGroup(int groupPosition) {
            return this.mExpandableController.expandGroup(groupPosition);
        }

        @Override
        public final boolean collapseGroup(int groupPosition) {
            return this.mExpandableController.collapseGroup(groupPosition);
        }

        @Override
        public final boolean isGroupExpanded(int groupPosition) {
            return this.mExpandableController.isGroupExpanded(groupPosition);
        }

        @Override
        public final void notifyHeadItemRangeInserted(int positionStart, int itemCount) {
            this.mExpandableController.notifyHeadItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public final void notifyHeadItemRangeRemoved(int positionStart, int itemCount) {
            this.mExpandableController.notifyHeadItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public final void notifyHeadItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.mExpandableController.notifyHeadItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public final void notifyTailItemRangeInserted(int positionStart, int itemCount) {
            this.mExpandableController.notifyTailItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public final void notifyTailItemRangeRemoved(int positionStart, int itemCount) {
            this.mExpandableController.notifyTailItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public final void notifyTailItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.mExpandableController.notifyTailItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public final void notifyEmptyItemRangeInserted(int positionStart, int itemCount) {
            this.mExpandableController.notifyEmptyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public final void notifyEmptyItemRangeRemoved(int positionStart, int itemCount) {
            this.mExpandableController.notifyEmptyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public final void notifyEmptyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            this.mExpandableController.notifyEmptyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public final void notifyGroupItemRangeExpanded(int groupPositionStart, int itemCount) {
            this.mExpandableController.notifyGroupItemRangeExpanded(groupPositionStart, itemCount);
        }

        @Override
        public final void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount) {
            this.mExpandableController.notifyGroupItemRangeInserted(groupPositionStart, itemCount);
        }

        @Override
        public final void notifyGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
            this.mExpandableController.notifyGroupItemRangeRemoved(groupPositionStart, itemCount);
        }

        @Override
        public final void notifyGroupItemRangeChanged(int groupPositionStart, int itemCount, @Nullable Object payload) {
            this.mExpandableController.notifyGroupItemRangeChanged(groupPositionStart, itemCount, payload);
        }

        @Override
        public final void notifyGroupItemRangeMoved(int fromGroupPosition, int toGroupPosition, int itemCount) {
            this.mExpandableController.notifyGroupItemRangeMoved(fromGroupPosition, toGroupPosition, itemCount);
        }

        @Override
        public final void notifyChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
            this.mExpandableController.notifyChildItemRangeInserted(groupPosition, childPositionStart, itemCount);
        }

        @Override
        public final void notifyChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
            this.mExpandableController.notifyChildItemRangeRemoved(groupPosition, childPositionStart, itemCount);
        }

        @Override
        public final void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, @Nullable Object payload) {
            this.mExpandableController.notifyChildItemRangeChanged(groupPosition, childPositionStart, itemCount, payload);
        }

        @Override
        public final void notifyChildItemRangeMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, int itemCount) {
            this.mExpandableController.notifyChildItemRangeMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition, itemCount);
        }

        final void dispatchOnGroupExpand(int groupPosition) {
            final ExpandableRecyclerView expandableRecyclerView = this.getRecyclerView();
            if (expandableRecyclerView != null) {
                expandableRecyclerView.dispatchOnGroupExpand(groupPosition);
            }
        }

        final void dispatchOnGroupCollapse(int groupPosition) {
            final ExpandableRecyclerView expandableRecyclerView = this.getRecyclerView();
            if (expandableRecyclerView != null) {
                expandableRecyclerView.dispatchOnGroupCollapse(groupPosition);
            }
        }
    }

    public static abstract class ItemDecoration extends SliverRecyclerView.ItemDecoration {
        @Override
        @SuppressLint("SwitchIntDef")
        public void getItemOffsets(@NonNull Rect outRect,
                                   @NonNull View itemView,
                                   @NonNull RecyclerView recyclerView,
                                   @NonNull RecyclerView.State state) {
            int positionType = PositionType.TYPE_GROUP;
            final RecyclerView.ViewHolder holder;
            holder = recyclerView.getChildViewHolder(itemView);
            if (holder instanceof ViewHolder) {
                positionType = ((ViewHolder) holder).getPositionType();
            }
            switch (positionType) {
                case PositionType.TYPE_HEAD:
                    this.getHeadItemOffsets(outRect, itemView, recyclerView, state);
                    break;
                case PositionType.TYPE_TAIL:
                    this.getTailItemOffsets(outRect, itemView, recyclerView, state);
                    break;
                case PositionType.TYPE_EMPTY:
                    this.getEmptyItemOffsets(outRect, itemView, recyclerView, state);
                    break;
                case PositionType.TYPE_GROUP:
                    this.getGroupItemOffsets(outRect, itemView, recyclerView, state);
                    break;
                case PositionType.TYPE_CHILD:
                    this.getChildItemOffsets(outRect, itemView, recyclerView, state);
                    break;
            }
        }

        public void getHeadItemOffsets(@NonNull Rect outRect,
                                       @NonNull View itemView,
                                       @NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.State state) {
            // nothing
        }

        public void getTailItemOffsets(@NonNull Rect outRect,
                                       @NonNull View itemView,
                                       @NonNull RecyclerView recyclerView,
                                       @NonNull RecyclerView.State state) {
            // nothing
        }

        public void getEmptyItemOffsets(@NonNull Rect outRect,
                                        @NonNull View itemView,
                                        @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.State state) {
            // nothing
        }

        public void getGroupItemOffsets(@NonNull Rect outRect,
                                        @NonNull View itemView,
                                        @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.State state) {
            // nothing
        }

        public void getChildItemOffsets(@NonNull Rect outRect,
                                        @NonNull View itemView,
                                        @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.State state) {
            // nothing
        }

        public final int getHeadItemCount(@NonNull RecyclerView recyclerView) {
            final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter instanceof Adapter) {
                return ((Adapter<?>) adapter).getHeadItemCount();
            }
            return 0;
        }

        public final int getTailItemCount(@NonNull RecyclerView recyclerView) {
            final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter instanceof Adapter) {
                return ((Adapter<?>) adapter).getTailItemCount();
            }
            return 0;
        }

        public final int getEmptyItemCount(@NonNull RecyclerView recyclerView) {
            final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter instanceof Adapter) {
                return ((Adapter<?>) adapter).getEmptyItemCount();
            }
            return 0;
        }

        public final int getGroupItemCount(@NonNull RecyclerView recyclerView) {
            final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter == null) {
                return 0;
            }
            if (adapter instanceof Adapter) {
                return ((Adapter<?>) adapter).getGroupItemCount();
            }
            return adapter.getItemCount();
        }

        public final int getChildItemCount(@NonNull RecyclerView recyclerView, int groupPosition) {
            final RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter instanceof Adapter) {
                return ((Adapter<?>) adapter).getChildItemCount(groupPosition);
            }
            return 0;
        }

        public final void offsetLeftAndRight(@NonNull Rect outRect, int spanCount, int spanIndex, int spaceSize) {
            this.offsetLeftAndRight(outRect, spanCount, spanIndex, spaceSize, false);
        }

        public final void offsetLeftAndRight(@NonNull Rect outRect,
                                             int spanCount,
                                             int spanIndex,
                                             int spaceSize, boolean border) {
            final int size = (spanCount + (border ? 1 : -1)) * spaceSize / spanCount;
            int interval;
            if (border) {
                interval = (spanIndex + 1) * spaceSize - (spanIndex * size);
            } else {
                interval = spanIndex * (spaceSize - size);
            }
            interval = Math.max(0, Math.min(interval, size));
            outRect.left = interval;
            outRect.right = size - interval;
        }

        public final void offsetTopAndBottom(@NonNull Rect outRect, int spanCount, int spanIndex, int spaceSize) {
            this.offsetTopAndBottom(outRect, spanCount, spanIndex, spaceSize, false);
        }

        public final void offsetTopAndBottom(@NonNull Rect outRect,
                                             int spanCount,
                                             int spanIndex,
                                             int spaceSize, boolean border) {
            final int size = (spanCount + (border ? 1 : -1)) * spaceSize / spanCount;
            int interval;
            if (border) {
                interval = (spanIndex + 1) * spaceSize - (spanIndex * size);
            } else {
                interval = spanIndex * (spaceSize - size);
            }
            interval = Math.max(0, Math.min(interval, size));
            outRect.top = interval;
            outRect.bottom = size - interval;
        }
    }

    public static class SavedState extends AbsSavedState {
        @NonNull
        private Parcelable mAdapterState;

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mAdapterState = EMPTY_STATE;
        }

        public SavedState(@NonNull Parcel source) {
            this(source, null);
        }

        public SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            final Parcelable adapterState = source.readParcelable(loader);
            this.mAdapterState = adapterState == null ? EMPTY_STATE : adapterState;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(this.mAdapterState, flags);
        }

        @NonNull
        public static final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(@NonNull Parcel source, @Nullable ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(@NonNull Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnAdapterChangedListener {

        void onAdapterChanged(@NonNull RecyclerView recyclerView,
                              @Nullable RecyclerView.Adapter<?> oldAdapter,
                              @Nullable RecyclerView.Adapter<?> newAdapter);
    }

    public interface OnGroupExpandListener {

        void onGroupExpand(@NonNull ExpandableRecyclerView parent, int groupPosition);
    }

    public interface OnGroupCollapseListener {

        void onGroupCollapse(@NonNull ExpandableRecyclerView parent, int groupPosition);
    }

    public interface OnItemClickListener<VH extends ViewHolder> {

        void onItemClick(@NonNull VH holder, @NonNull View target, int position);
    }

    public interface OnItemLongClickListener<VH extends ViewHolder> {

        boolean onItemLongClick(@NonNull VH holder, @NonNull View target, int position);
    }

    public interface OnChildItemClickListener<VH extends ViewHolder> {

        void onChildItemClick(@NonNull VH holder, @NonNull View target, int groupPosition, int childPosition);
    }

    public interface OnChildItemLongClickListener<VH extends ViewHolder> {

        boolean onChildItemLongClick(@NonNull VH holder, @NonNull View target, int groupPosition, int childPosition);
    }
}
