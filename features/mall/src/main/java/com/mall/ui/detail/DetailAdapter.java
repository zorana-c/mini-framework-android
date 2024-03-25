package com.mall.ui.detail;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.framework.core.ui.adapter.UIExpandableAdapter;
import com.mall.R;

/**
 * @Author create by Zhengzelong on 2024-03-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DetailAdapter extends UIExpandableAdapter<String> {
    @NonNull
    @Override
    public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
        if (itemViewType == 1) {
            return this.inflate(R.layout.item_commodity_info_layout);
        }
        if (itemViewType == 2) {
            return this.inflate(R.layout.item_commodity_shop_layout);
        }
        return super.onCreateGroupItemView(parent, itemViewType);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition) {
        final int itemViewType = holder.getRetItemViewType();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        if (groupPosition == 0) {
            return 1;
        }
        if (groupPosition == 1) {
            return 2;
        }
        return super.getGroupItemViewType(groupPosition);
    }

    @Override
    public int getGroupItemCount() {
        return 2;
    }
}
