package com.common.ui.adapter.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.common.R;
import com.common.bean.Commodity;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.widget.UIImageView;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-03-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityViewHolder extends UIViewHolder<Commodity> {
    @NonNull
    public static CommodityViewHolder create(@NonNull LayoutInflater inflater,
                                             @NonNull ViewGroup parent) {
        final View itemView;
        itemView = inflater.inflate(R.layout.item_commodity_layout, parent, false);
        return new CommodityViewHolder(itemView);
    }

    @NonNull
    private final UIImageView cover;

    public CommodityViewHolder(@NonNull View itemView) {
        super(itemView);
        this.cover = this.requireViewById(R.id.cover);
    }

    @Override
    public void onInit(@NonNull List<Object> payloads) {
        super.onInit(payloads);
        this.cover.setImageUrl("https://img0.baidu.com/it/u=195169129,3351167253&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1711472400&t=c01455ade7e16a8bfd3ecd47d6e49bf9");
    }
}
