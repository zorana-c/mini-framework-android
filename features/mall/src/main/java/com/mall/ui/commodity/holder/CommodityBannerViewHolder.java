package com.mall.ui.commodity.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.common.ui.adapter.BannerAdapter;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.util.UITestUtils;
import com.framework.widget.recycler.banner.BannerIndicatorView;
import com.framework.widget.recycler.banner.BannerRecyclerView;
import com.mall.R;
import com.mall.bean.Banner;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-03-26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityBannerViewHolder extends UIViewHolder {
    @NonNull
    public static CommodityBannerViewHolder create(@NonNull LayoutInflater inflater,
                                                   @NonNull ViewGroup parent) {
        final View itemView;
        itemView = inflater.inflate(R.layout.item_commodity_banner_layout, parent, false);
        return new CommodityBannerViewHolder(itemView);
    }

    @NonNull
    private final BannerRecyclerView bannerRecyclerView;
    @NonNull
    private final BannerIndicatorView bannerIndicatorView;

    public CommodityBannerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.bannerRecyclerView = this.requireViewById(R.id.bannerRecyclerView);
        this.bannerIndicatorView = this.requireViewById(R.id.bannerIndicatorView);
    }

    @Override
    public void onInit(@NonNull List<Object> payloads) {
        super.onInit(payloads);
        final BannerRecyclerView bannerRecyclerView = this.bannerRecyclerView;
        BannerAdapter<Banner> adapter;
        adapter = (BannerAdapter<Banner>) bannerRecyclerView.getAdapter();
        if (adapter == null) {
            adapter = new BannerAdapter<>();

            final LifecycleOwner owner;
            owner = this.requireUIPageController().getUIComponent();
            bannerRecyclerView.setHasFixedSize(true);
            bannerRecyclerView.setLifecycle(owner);
            bannerRecyclerView.setAdapter(adapter);
            this.bannerIndicatorView.attachToRecyclerView(bannerRecyclerView);
        }
        adapter.getUIListDataController().setAll(UITestUtils.obtain(1, 5));
    }
}
