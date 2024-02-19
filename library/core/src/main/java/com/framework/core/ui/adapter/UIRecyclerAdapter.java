package com.framework.core.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIDataController;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIRecyclerAdapter<T>
        extends RecyclerView.Adapter<UIRecyclerAdapter.ViewHolder> {
    @NonNull
    private final UIDataController<T> mUIListDataController;

    public UIRecyclerAdapter() {
        final ComponentListener<T> componentListener;
        componentListener = new ComponentListener<>(this);
        this.mUIListDataController = new UIDataController<>();
        this.mUIListDataController.registerDataObserver(componentListener);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new ViewHolder(this.onCreateItemView(parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateItemView(@NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Override
    public int getItemCount() {
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

    private static final class ComponentListener<T> implements
            UIDataController.DataObserver {
        @NonNull
        private final UIRecyclerAdapter<T> mUIRecyclerAdapter;

        ComponentListener(@NonNull UIRecyclerAdapter<T> uiRecyclerAdapter) {
            this.mUIRecyclerAdapter = uiRecyclerAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean onItemRangeInserted(int positionStart, int itemCount) {
            final UIRecyclerAdapter<T> adapter = this.mUIRecyclerAdapter;
            final int listItemCount = adapter.getItemCount();
            if (listItemCount == itemCount) {
                adapter.notifyDataSetChanged();
            } else {
                final int cItemCount = listItemCount - positionStart;
                adapter.notifyItemRangeInserted(positionStart, itemCount);
                adapter.notifyItemRangeChanged(positionStart, cItemCount);
            }
            return true;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean onItemRangeRemoved(int positionStart, int itemCount) {
            final UIRecyclerAdapter<T> adapter = this.mUIRecyclerAdapter;
            final int listItemCount = adapter.getItemCount();
            if (listItemCount == 0) {
                adapter.notifyDataSetChanged();
            } else {
                final int cItemCount = listItemCount - positionStart;
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
                adapter.notifyItemRangeChanged(positionStart, cItemCount);
            }
            return true;
        }

        @Override
        public boolean onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            final UIRecyclerAdapter<T> adapter = this.mUIRecyclerAdapter;
            final int positionStart = Math.min(fromPosition, toPosition);
            final int cItemCount = Math.abs(fromPosition - toPosition) + 1;
            adapter.notifyItemMoved(fromPosition, toPosition);
            adapter.notifyItemRangeChanged(positionStart, cItemCount);
            return true;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
