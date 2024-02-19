package com.framework.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.framework.core.rx.view.RxView;
import com.framework.widget.expand.ExpandableRecyclerView;
import com.framework.widget.recycler.banner.BannerIndicatorView;
import com.framework.widget.recycler.banner.BannerLayoutManager;
import com.framework.widget.recycler.banner.BannerRecyclerView;
import com.framework.widget.recycler.banner.MarqueeLayoutManager;
import com.framework.widget.recycler.pager.DepthPageTransformer;
import com.framework.widget.sliver.SliverRefreshLayout;
import com.framework.core.compat.UIToast;
import com.framework.core.ui.abs.UIDecorFragment;

/**
 * @Author create by Zhengzelong on 2023-02-17
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_sliver;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final SliverRefreshLayout pinned;
        pinned = this.requireViewById(R.id.pinned);
        pinned.setRefreshCallback((sliverRefreshLayout, locate) -> {
            sliverRefreshLayout.postDelayed(() -> {
                sliverRefreshLayout.completeRefreshed(720L);
            }, 2000);
        });

        RxView.of(this).click(it -> {
            pinned.forcedRefreshing();
        }, R.id.refresh);

        final BannerLayoutManager bannerLayoutManager;
        bannerLayoutManager = new BannerLayoutManager(this.requireContext());
        bannerLayoutManager.setPageTransformer(new DepthPageTransformer());
        bannerLayoutManager.setLifecycle(this);

        final BannerRecyclerView bannerRecyclerView;
        bannerRecyclerView = this.requireViewById(R.id.bannerRecyclerView);
        bannerRecyclerView.setLayoutManager(bannerLayoutManager);
        bannerRecyclerView.setAdapter(adapter);
        bannerRecyclerView.setOnGroupItemClickListener((holder, target, position) -> {
            UIToast.asyncToast(String.valueOf(position));
        });

        final BannerIndicatorView bannerIndicatorView;
        bannerIndicatorView = this.requireViewById(R.id.bannerIndicatorView);
        bannerIndicatorView.attachToRecyclerView(bannerRecyclerView);

        // mid

        final MarqueeLayoutManager marqueeLayoutManager;
        marqueeLayoutManager = new MarqueeLayoutManager(this.requireContext());
        marqueeLayoutManager.setPlayDelayMillis(2000L);
        marqueeLayoutManager.setLifecycle(this);

        final BannerRecyclerView marqueeRecyclerView;
        marqueeRecyclerView = this.requireViewById(R.id.marqueeRecyclerView);
        marqueeRecyclerView.setLayoutManager(marqueeLayoutManager);
        marqueeRecyclerView.setUserScrollEnabled(false);
        marqueeRecyclerView.setAdapter(adapter2);
        marqueeRecyclerView.setOnGroupItemClickListener((holder, target, position) -> {
            UIToast.asyncToast(String.valueOf(position));
        });
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    private final Adapter adapter = new Adapter();

    static class Adapter extends ExpandableRecyclerView.Adapter<ExpandableRecyclerView.ViewHolder> {
        final String[] _urls = new String[]{
                "https://img0.baidu.com/it/u=1483946392,80414132&fm=253&fmt=auto&app=138&f=JPEG?w=1200&h=500",
                "https://img0.baidu.com/it/u=2604863857,3097058401&fm=253&fmt=auto&app=138&f=JPEG?w=1280&h=433",
                "https://img2.baidu.com/it/u=1961302154,246146198&fm=253&fmt=auto&app=138&f=JPEG?w=1600&h=500",
                "https://img1.baidu.com/it/u=2757935014,2054677376&fm=253&fmt=auto&app=138&f=JPEG?w=1280&h=367",
                "https://img1.baidu.com/it/u=4064761558,1446081750&fm=253&fmt=auto&app=138&f=JPEG?w=1053&h=390",
                "https://img2.baidu.com/it/u=3169179090,881607947&fm=253&fmt=auto&app=138&f=JPEG?w=1440&h=500",
                "https://img2.baidu.com/it/u=1524389572,4044551254&fm=253&fmt=auto&app=138&f=JPEG?w=1338&h=500",
                "https://img2.baidu.com/it/u=192846542,3866316605&fm=253&fmt=auto&app=138&f=JPEG?w=1209&h=500",
        };

        @Override
        public ExpandableRecyclerView.ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            return new ExpandableRecyclerView.ViewHolder(this.inflate(R.layout.item_match_layout, parent)) {
            };
        }

        @Override
        public void onBindGroupViewHolder(@NonNull ExpandableRecyclerView.ViewHolder holder, int groupPosition) {
            final ImageView imageView = holder.requireViewById(R.id.image);
            Glide.with(imageView)
                    .load(_urls[groupPosition])
                    .into(imageView);
        }

        @Override
        public int getGroupItemCount() {
            return _urls.length;
        }
    }

    private final Adapter2 adapter2 = new Adapter2();

    static class Adapter2 extends ExpandableRecyclerView.Adapter<ExpandableRecyclerView.ViewHolder> {
        @Override
        public ExpandableRecyclerView.ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int itemViewType) {
            return new ExpandableRecyclerView.ViewHolder(this.inflate(R.layout.item_match_2_layout, parent)) {
            };
        }

        @Override
        public void onBindGroupViewHolder(@NonNull ExpandableRecyclerView.ViewHolder holder, int groupPosition) {
            final TextView textView = holder.requireViewById(R.id.text);
            textView.setText(String.format("This is marquee text by %s position", groupPosition));
        }

        @Override
        public int getGroupItemCount() {
            return 5;
        }
    }
}
