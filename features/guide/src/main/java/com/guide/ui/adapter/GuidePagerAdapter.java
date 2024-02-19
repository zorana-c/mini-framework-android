package com.guide.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.framework.core.ui.adapter.UIRecyclerAdapter;
import com.framework.core.widget.UIImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class GuidePagerAdapter extends UIRecyclerAdapter<Integer> {
    @NonNull
    @Override
    public View onCreateItemView(@NonNull ViewGroup parent, int itemViewType) {
        final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        final UIImageView itemView = new UIImageView(parent.getContext());
        itemView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        itemView.setLayoutParams(layoutParams);
        return itemView;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int groupPosition) {
        final Integer resId = this.findDataSourceBy(groupPosition);
        final UIImageView imageView = (UIImageView) holder.itemView;
        imageView.setImage(resId);
    }

    public void setDataSources(@NonNull int[] resIds) {
        final List<Integer> resIdList = new ArrayList<>(resIds.length);
        for (int resId : resIds) {
            resIdList.add(resId);
        }
        this.getUIListDataController().setAll(resIdList);
    }
}
