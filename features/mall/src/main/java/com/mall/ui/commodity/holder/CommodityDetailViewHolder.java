package com.mall.ui.commodity.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.framework.core.ui.abs.UIViewHolder;
import com.mall.R;
import com.mall.bean.Commodity;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-03-26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityDetailViewHolder<T extends Commodity> extends UIViewHolder<T> {
    @NonNull
    public static <T extends Commodity> CommodityDetailViewHolder<T> create(@NonNull LayoutInflater inflater,
                                                                            @NonNull ViewGroup parent) {
        final View itemView;
        itemView = inflater.inflate(R.layout.item_commodity_detail_layout, parent, false);
        return new CommodityDetailViewHolder<>(itemView);
    }

    public CommodityDetailViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onInit(@NonNull List<Object> payloads) {
        super.onInit(payloads);
    }
}
