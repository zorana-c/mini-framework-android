package com.framework.core.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.core.content.UIExpandableObserver;
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
    private final UIExpandableObserver mUIExpandableObserver;
    @NonNull
    private final UIDataController<T> mUIListDataController;

    public UIExpandableAdapter() {
        this.mUIExpandableObserver = new UIExpandableObserver(this);
        this.mUIListDataController = new UIDataController<>();
        this.mUIListDataController.registerObserver(this.mUIExpandableObserver);
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
    public final <R extends T> R findDataBy(int position) {
        return this.getUIListDataController().findBy(position);
    }

    @NonNull
    public final <R extends T> R requireDataBy(int position) {
        return this.getUIListDataController().requireBy(position);
    }

    public boolean getGroupExpanded() {
        return this.mUIExpandableObserver.getGroupExpanded();
    }

    public void setGroupExpanded(boolean groupExpanded) {
        this.mUIExpandableObserver.setGroupExpanded(groupExpanded);
    }

    public static class ViewHolder extends ExpandableRecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
