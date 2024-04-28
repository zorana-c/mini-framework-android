package com.framework.core.ui.abs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.content.UIListController;
import com.framework.widget.expand.ExpandableRecyclerView;

/**
 * @Author create by Zhengzelong on 2021/11/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIListFragmentActivity<T> extends UIDecorFragmentActivity
        implements UIListController.UIComponent<T>,
        UIListController.Callback<UIViewHolder>,
        ExpandableRecyclerView.OnItemClickListener<UIViewHolder>,
        ExpandableRecyclerView.OnItemLongClickListener<UIViewHolder>,
        ExpandableRecyclerView.OnChildItemClickListener<UIViewHolder>,
        ExpandableRecyclerView.OnChildItemLongClickListener<UIViewHolder> {
    @NonNull
    private final UIListController<T>
            mUIListController = new UIListController<>(this);

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    @CallSuper
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        this.getUIPageController()
                .setOnItemClickListener(this)
                .setOnItemLongClickListener(this)
                .setOnChildItemClickListener(this)
                .setOnChildItemLongClickListener(this);
    }

    /**
     * @deprecated 已舍弃
     */
    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        // no-op
    }

    // Item.

    @NonNull
    @Override
    public UIViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                           @NonNull ViewGroup parent, int itemViewType) {
        return new UIViewHolder(this.onCreateItemView(inflater, parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateItemView(@NonNull LayoutInflater inflater,
                                 @NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder holder, int position) {
        // no-op
    }

    @Override
    public void onItemClick(@NonNull UIViewHolder holder, @NonNull View target, int position) {
        // no-op
    }

    @Override
    public boolean onItemLongClick(@NonNull UIViewHolder holder, @NonNull View target, int position) {
        // no-op
        return false;
    }

    // Child item.

    @NonNull
    @Override
    public UIViewHolder onCreateChildViewHolder(@NonNull LayoutInflater inflater,
                                                @NonNull ViewGroup parent, int itemViewType) {
        return new UIViewHolder(this.onCreateChildItemView(inflater, parent, itemViewType)) {
        };
    }

    @NonNull
    public View onCreateChildItemView(@NonNull LayoutInflater inflater,
                                      @NonNull ViewGroup parent, int itemViewType) {
        throw new NullPointerException("ERROR");
    }

    @Override
    public void onBindChildViewHolder(@NonNull UIViewHolder holder, int groupPosition, int childPosition) {
        // no-op
    }

    @Override
    public void onChildItemClick(@NonNull UIViewHolder holder, @NonNull View target, int groupPosition, int childPosition) {
        // no-op
    }

    @Override
    public boolean onChildItemLongClick(@NonNull UIViewHolder holder, @NonNull View target, int groupPosition, int childPosition) {
        // no-op
        return false;
    }

    @NonNull
    @Override
    public UIListController<T> getUIPageController() {
        return this.mUIListController;
    }
}