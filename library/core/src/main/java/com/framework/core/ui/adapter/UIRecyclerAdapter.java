package com.framework.core.ui.adapter;

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
        extends RecyclerView.Adapter<UIRecyclerAdapter.ViewHolder>
        implements UIDataController.Adapter {
    @NonNull
    private final UIDataController<T> mUIListDataController;

    public UIRecyclerAdapter() {
        this.mUIListDataController = new UIDataController<>(this);
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
    public final <R extends T> R findDataBy(int position) {
        return this.getUIListDataController().findBy(position);
    }

    @NonNull
    public final <R extends T> R requireDataBy(int position) {
        return this.getUIListDataController().requireBy(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
