package com.framework.common.ui.picker.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.common.R;
import com.framework.common.ui.picker.bean.UINode;
import com.framework.core.content.UIDataController;
import com.framework.core.content.UIDataControllers;
import com.framework.widget.recycler.picker.AppCompatPickerView;

/**
 * @Author create by Zhengzelong on 2023-04-04
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UINodePickerAdapter<T extends UINode<?>> extends UIPickerAdapter<UIPickerViewHolder> {
    @NonNull
    private final UIDataController<T> uiDataController;
    @Nullable
    private UINode<? super T> upstreamNode;
    // 是否允许不限选择
    private boolean allowUnlimitedEnabled;

    public UINodePickerAdapter() {
        this(null);
    }

    public UINodePickerAdapter(@Nullable AppCompatPickerView upstream) {
        super(upstream);
        this.uiDataController = new UIDataController<>();
        this.uiDataController.registerDataObserver(UIDataControllers.observer(this));
    }

    @Nullable
    @Override
    public UIPickerViewHolder onCreateHeadViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new UIPickerViewHolder(this.inflate(R.layout.ui_item_picker_layout, parent));
    }

    @Nullable
    @Override
    public UIPickerViewHolder onCreateEmptyViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new UIPickerViewHolder(this.inflate(R.layout.ui_item_picker_layout, parent));
    }

    @Nullable
    @Override
    public UIPickerViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
        return new UIPickerViewHolder(this.inflate(R.layout.ui_item_picker_layout, parent));
    }

    @Override
    public void onBindHeadViewHolder(@NonNull UIPickerViewHolder holder, int position) {
        final TextView textView = holder.requireViewById(android.R.id.text1);
        textView.setText("不限");
    }

    @Override
    public void onBindEmptyViewHolder(@NonNull UIPickerViewHolder holder, int position) {
        final TextView textView = holder.requireViewById(android.R.id.text1);
        textView.setText(null);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull UIPickerViewHolder holder, int groupPosition) {
        final TextView textView = holder.requireViewById(android.R.id.text1);
        textView.setText(this.requireDataSourceBy(groupPosition).toString());
    }

    @Override
    public int getHeadItemCount() {
        if (this.allowUnlimitedEnabled) {
            return 1;
        }
        return super.getHeadItemCount();
    }

    @Override
    public int getEmptyItemCount() {
        if (!this.allowUnlimitedEnabled) {
            return 1;
        }
        return super.getEmptyItemCount();
    }

    @Override
    public int getGroupItemCount() {
        return this.uiDataController.size();
    }

    public boolean isAllowUnlimitedEnabled() {
        return this.allowUnlimitedEnabled;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAllowUnlimitedEnabled(boolean enabled) {
        if (this.allowUnlimitedEnabled != enabled) {
            this.allowUnlimitedEnabled = enabled;
            this.notifyDataSetChanged();
        }
    }

    @Nullable
    public final <R extends T> R findDataSourceBy(int position) {
        return this.uiDataController.findDataSourceBy(position);
    }

    @NonNull
    public final <R extends T> R requireDataSourceBy(int position) {
        return this.uiDataController.requireDataSourceBy(position);
    }

    @NonNull
    public final <R extends T> UIDataController<R> getUIDataController() {
        return (UIDataController<R>) this.uiDataController;
    }

    public final void setDataSources(@Nullable UINode<? super T> upstreamNode) {
        this.setDataSources(upstreamNode, RecyclerView.NO_POSITION);
    }

    public void setDataSources(@Nullable UINode<? super T> upstreamNode, int defPosition) {
        final UINode<? super T> oldNode = this.upstreamNode;
        if (oldNode == upstreamNode) {
            return;
        }
        if (oldNode != null) {
            this.uiDataController.clear();
        }
        this.upstreamNode = upstreamNode;
        if (upstreamNode != null) {
            this.uiDataController.setAll(upstreamNode.getDownstream());
        }
        if (defPosition == RecyclerView.NO_POSITION) {
            return;
        }
        final AppCompatPickerView thisV = this.getRecyclerView();
        if (thisV != null) {
            thisV.stopScroll();
            thisV.setCurrentPosition(defPosition, false);
        }
    }

    @Override
    public void onUpstreamPositionChanged(int upstreamPosition) {
        if (!this.hasUpstream()) {
            return;
        }
        if (upstreamPosition == RecyclerView.NO_POSITION) {
            this.setDataSources(null);
            return;
        }
        final UINodePickerAdapter<?> upstreamAd = this.requireUpstreamAd();
        final int position;
        position = upstreamPosition - upstreamAd.getHeadItemCount();
        final UINode<? super T> upstreamNode;
        upstreamNode = (UINode<? super T>) upstreamAd.findDataSourceBy(position);
        this.setDataSources(upstreamNode);
    }
}
