package com.mall.ui.commodity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mall.bean.Commodity;
import com.mall.ui.commodity.holder.CommodityViewHolder;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.util.UITestUtils;
import com.mall.R;

/**
 * @Author create by Zhengzelong on 2024-03-22
 * @Email : 171905184@qq.com
 * @Description : 商品详情界面
 */
public class CommodityDetailFragment extends UIListFragment<Commodity> {
    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        this.getUIPageController().postDelayed(() -> {
            this.putAll(UITestUtils.obtain(page, limit));
        }, 1500);
    }

    @NonNull
    @Override
    public UIViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                           @NonNull ViewGroup parent, int itemViewType) {
        return CommodityViewHolder.create(inflater, parent);
    }

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_commodity_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
    }
}
