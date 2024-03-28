package com.common.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.common.R;
import com.common.bean.IBanner;
import com.framework.core.ui.adapter.UIExpandableAdapter;
import com.framework.core.widget.UIImageView;

/**
 * @Author create by Zhengzelong on 2024-03-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class BannerAdapter<T extends IBanner> extends UIExpandableAdapter<T> {
    @NonNull
    @Override
    public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
        return this.inflate(R.layout.item_banner_layout);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition) {
        // final T banner = this.requireDataBy(groupPosition);

        final UIImageView cover = holder.requireViewById(R.id.cover);
        cover.setImageUrl("https://img0.baidu.com/it/u=195169129,3351167253&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1711472400&t=c01455ade7e16a8bfd3ecd47d6e49bf9");
    }
}
