package com.framework.core.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIDataController;
import com.framework.widget.expand.ExpandableRecyclerView;

/**
 * @Author create by Zhengzelong on 2022/3/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIExpandableAdapter<T>
        extends ExpandableRecyclerView.Adapter<UIExpandableAdapter.ViewHolder> {
    @NonNull
    private final ComponentListener<T> mComponentListener;
    @NonNull
    private final UIDataController<T> mUIListDataController;

    public UIExpandableAdapter() {
        this.mComponentListener = new ComponentListener<>(this);
        this.mUIListDataController = new UIDataController<>();
        this.mUIListDataController.registerDataObserver(this.mComponentListener);
        this.mUIListDataController.addOnDataChangedListener(this.mComponentListener);
    }

    @Override
    @CallSuper
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    @CallSuper
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public ViewHolder onCreateHeadViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new ViewHolder(this.onCreateHeadItemView(parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateHeadItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Nullable
    @Override
    public ViewHolder onCreateTailViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new ViewHolder(this.onCreateTailItemView(parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateTailItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Nullable
    @Override
    public ViewHolder onCreateEmptyViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new ViewHolder(this.onCreateEmptyItemView(parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateEmptyItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @NonNull
    @Override
    public ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new ViewHolder(this.onCreateGroupItemView(parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @NonNull
    @Override
    public ViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int childItemViewType) {
        return new ViewHolder(this.onCreateChildItemView(parent, childItemViewType)) {
        };
    }

    @NonNull
    public View onCreateChildItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Override
    public int getGroupItemCount() {
        return this.getUIListDataController().size();
    }

    @NonNull
    public UIDataController<T> getUIListDataController() {
        return this.mUIListDataController;
    }

    @Nullable
    public final <R extends T> R findDataSourceBy(int position) {
        return this.getUIListDataController().findDataSourceBy(position);
    }

    @NonNull
    public final <R extends T> R requireDataSourceBy(int position) {
        return this.getUIListDataController().requireDataSourceBy(position);
    }

    public void setGroupDefaultExpanded(boolean groupDefaultExpanded) {
        this.mComponentListener.setGroupDefaultExpanded(groupDefaultExpanded);
    }

    private static final class ComponentListener<T> implements
            UIDataController.DataObserver,
            UIDataController.OnDataChangedListener<T> {
        @NonNull
        private final UIExpandableAdapter<T> mUIExpandableAdapter;
        private boolean mGroupDefaultExpanded;

        ComponentListener(@NonNull UIExpandableAdapter<T> uiExpandableAdapter) {
            this.mUIExpandableAdapter = uiExpandableAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean onItemRangeInserted(int positionStart, int itemCount) {
            final UIExpandableAdapter<T> adapter = this.mUIExpandableAdapter;
            final int gItemCount = adapter.getGroupItemCount();
            if (gItemCount == itemCount) {
                adapter.notifyDataSetChanged();
            } else {
                final int cItemCount = gItemCount - positionStart;
                final int tItemCount = adapter.getTailItemCount();
                adapter.notifyGroupItemRangeInserted(positionStart, itemCount);
                adapter.notifyGroupItemRangeChanged(positionStart, cItemCount);
                adapter.notifyTailItemRangeChanged(0, tItemCount);
            }
            return true;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean onItemRangeRemoved(int positionStart, int itemCount) {
            final UIExpandableAdapter<T> adapter = this.mUIExpandableAdapter;
            final int gItemCount = adapter.getGroupItemCount();
            if (gItemCount == 0) {
                adapter.notifyDataSetChanged();
            } else {
                final int cItemCount = gItemCount - positionStart;
                final int tItemCount = adapter.getTailItemCount();
                adapter.notifyGroupItemRangeRemoved(positionStart, itemCount);
                adapter.notifyGroupItemRangeChanged(positionStart, cItemCount);
                adapter.notifyTailItemRangeChanged(0, tItemCount);
            }
            return true;
        }

        @Override
        public boolean onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            final UIExpandableAdapter<T> adapter = this.mUIExpandableAdapter;
            final int positionStart = Math.min(fromPosition, toPosition);
            final int cItemCount = Math.abs(fromPosition - toPosition) + 1;
            adapter.notifyGroupItemRangeMoved(fromPosition, toPosition, itemCount);
            adapter.notifyGroupItemRangeChanged(positionStart, cItemCount);
            return true;
        }

        @Override
        public void onItemRangeInserted(@NonNull UIDataController<T> uiDataController,
                                        int positionStart,
                                        int itemCount) {
            if (this.mGroupDefaultExpanded) {
                this.mUIExpandableAdapter.expandGroup(positionStart, itemCount);
            }
        }

        public void setGroupDefaultExpanded(boolean groupDefaultExpanded) {
            this.mGroupDefaultExpanded = groupDefaultExpanded;
        }
    }

    public static class ViewHolder extends ExpandableRecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
