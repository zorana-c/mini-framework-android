package com.framework.demo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.framework.core.compat.UIRes;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.listener.OnTabSelectedListener;
import com.framework.core.ui.abs.UIDecorFragment;
import com.framework.core.ui.adapter.UIExpandableAdapter;
import com.framework.core.util.UITestUtils;
import com.framework.core.widget.UIImageView;
import com.framework.demo.widget.CustomRefreshLoadView;
import com.framework.widget.ScrollingIndicatorView;
import com.framework.widget.compat.UIViewCompat;
import com.framework.widget.recycler.banner.BannerIndicatorView;
import com.framework.widget.recycler.banner.BannerLayoutManager;
import com.framework.widget.recycler.banner.BannerRecyclerView;
import com.framework.widget.sliver.SliverContainer;
import com.framework.widget.sliver.SliverRefreshLayout;
import com.framework.widget.sliver.SliverViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;

/**
 * @Author create by Zhengzelong on 2023-06-27
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverFragment2 extends UIDecorFragment {
    private static final String[] _urls = new String[]{
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
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_sliver_2;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIDecorController dc = this.getUIPageController();

        final UIActionBarController abc = dc.getUIActionBarController();
        abc.setEnabled(false);

        this.init();
        this.initBanner();
        this.initHorList();
        this.initClassify();
        this.initViewPager();
        this.initContainer();
        this.initRefreshView();
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        final SliverRefreshLayout srl;
        srl = this.requireViewById(R.id.sliverRefreshLayout);
        srl.postDelayed(() -> {
            // 通知视图: 完成刷新(延迟: 850毫秒)
            srl.completeRefreshed(850L);
        }, 2500);
    }

    private void init() {
        final UIImageView iv;
        iv = this.requireViewById(R.id.backgroundView);
        iv.setImageUrl("https://img2.baidu.com/it/u=3017249623,2418994156&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500");
    }

    private void initBanner() {
        final Context c = this.requireContext();
        final BannerRecyclerView brv;
        brv = this.requireViewById(R.id.bannerRecyclerView);

        BannerLayoutManager blm = (BannerLayoutManager) brv.getLayoutManager();
        if (blm == null) {
            blm = new BannerLayoutManager(c);

            brv.setLayoutManager(blm);
        }
        blm.setLifecycle(this);

        SliverBannerAdapter sba = (SliverBannerAdapter) brv.getAdapter();
        if (sba == null) {
            sba = new SliverBannerAdapter();

            brv.setHasFixedSize(true);
            brv.setAdapter(sba);
        }
        sba.getUIListDataController().setAll(Arrays.asList(_urls));

        final BannerIndicatorView biv;
        biv = this.requireViewById(R.id.bannerIndicatorView);
        biv.attachToRecyclerView(brv);
    }

    private void initHorList() {
        final RecyclerView rv;
        rv = this.requireViewById(R.id.list1);

        HorListAdapter ad = (HorListAdapter) rv.getAdapter();
        if (ad == null) {
            ad = new HorListAdapter();

            rv.setHasFixedSize(true);
            rv.setAdapter(ad);
        }
        ad.getUIListDataController().setAll(UITestUtils.obtain(1, 10));
    }

    private void initClassify() {
        final RecyclerView rv;
        rv = this.requireViewById(R.id.classifyRecyclerView);

        ClassifyHorListAdapter ad = (ClassifyHorListAdapter) rv.getAdapter();
        if (ad == null) {
            ad = new ClassifyHorListAdapter();

            rv.setHasFixedSize(true);
            rv.setAdapter(ad);
        }
        ad.getUIListDataController().setAll(UITestUtils.obtain(1, 16));

        final ScrollingIndicatorView siv;
        siv = this.requireViewById(R.id.classifyIndicatorView);
        siv.setRecyclerView(rv);
    }

    private void initViewPager() {
        final SliverPagerAdapter spa;
        spa = new SliverPagerAdapter(this.getChildFragmentManager());

        final SliverViewPager svp;
        svp = this.requireViewById(R.id.sliverViewPager);
        svp.setUserScrollEnabled(false);
        svp.setAdapter(spa);

        final TabLayout tl;
        tl = this.requireViewById(R.id.tabLayout);
        tl.addOnTabSelectedListener(new ComponentListener());
        tl.setupWithViewPager(svp);
        // 不需要转场动画效果(必须在设置适配器之后再添加监听事件)
        tl.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                svp.setCurrentItem(tab.getPosition(), false);
            }
        });
    }

    private void initContainer() {
        final SliverContainer sc;
        sc = this.requireViewById(R.id.sliverContainer);
        sc.addOnScrollListener(new ContainerListener());
    }

    private void initRefreshView() {
        final SliverRefreshLayout srl;
        srl = this.requireViewById(R.id.sliverRefreshLayout);
        srl.setBounceLocate(SliverContainer.SCROLL_LOCATE_HEAD);
        srl.setRefreshLocate(SliverContainer.SCROLL_LOCATE_HEAD);
        srl.addOnScrollListener(new ComponentScrollListener());
        srl.setRefreshCallback((sliverRefreshLayout, locate) -> {
            final SliverViewPager svp;
            svp = this.requireViewById(R.id.sliverViewPager);
            final SliverPagerAdapter spa;
            spa = (SliverPagerAdapter) svp.getAdapter();
            if (spa != null) {
                spa.notifyDataSetRefresh();
            }
            this.getUIPageController().notifyDataSetRefresh();
        });
        // 自定义头部刷新视图.
        srl.setHeadLoadView(new CustomRefreshLoadView(srl.getContext()));

        // 设置头部刷新手势样式.
        // final SliverRefreshLoadLayout rll;
        // rll = srl.requireHeadLoadView();
        // rll.setScrollStyle(SliverRefreshLoadLayout.SCROLL_STYLE_FAST);

        final Context c = srl.getContext();
        final SliverContainer.LayoutParams lp;
        lp = (SliverContainer.LayoutParams) srl.getLayoutParams();
        // 系统状态栏 height.
        lp.heightUsed += UIViewCompat.getStatusBarHeight(c);
        // ActionBar height.
        lp.heightUsed += UIViewCompat.dip2px(c, 46);
    }

    private final class ComponentListener extends OnTabSelectedListener {
        private int position = 0;

        @Override
        public void onTabSelected(@NonNull TabLayout.Tab tab) {
            final int position = tab.getPosition();
            if (this.position != position) {
                this.position = position;
                this.scrollToEnd();
            }
        }

        private void scrollToEnd() {
            final SliverRefreshLayout srl;
            srl = requireViewById(R.id.sliverRefreshLayout);
            final int r = srl.computeVerticalScrollRange();
            final int o = srl.computeVerticalScrollOffset();
            final int e = srl.computeVerticalScrollExtent();
            // 不带有动画效果
            // nrl.scrollBy(0, r - o - e);
            // 带有动画效果(缺点: 会受到手势影响)
            srl.smoothScrollBy(0, r - o - e);
        }
    }

    private final class ContainerListener
            extends SliverContainer.SimpleOnScrollListener {
        @NonNull
        private final View tabContainer;

        public ContainerListener() {
            this.tabContainer = requireViewById(R.id.tabContainer);
        }

        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            final int offset = sliverContainer.computeVerticalScrollOffset();
            final int ranged = UIRes.dip2px(39); // TabLayout the height.
            final float scale;
            scale = Math.min(Math.max(0.f, offset) / (float) ranged, 1.f);
            // Setup view alpha.
            this.tabContainer.setAlpha(1.f - scale);
        }
    }

    private final class ComponentScrollListener
            extends SliverContainer.SimpleOnScrollListener {
        @NonNull
        private final View bannerRecyclerView;
        @NonNull
        private final View actionBarContainer;

        public ComponentScrollListener() {
            this.bannerRecyclerView = requireViewById(R.id.bannerRecyclerView);
            this.actionBarContainer = requireViewById(R.id.actionBarContainer);
        }

        @Override
        public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
            final int offset = sliverContainer.computeVerticalScrollOffset();
            final int extent = this.getDecoratedExtent(this.bannerRecyclerView);
            final float scale;
            scale = Math.min(extent > 0 ? Math.max(0.f, offset) / (float) extent : 0.f, 1.f);

            final Context c = sliverContainer.getContext();
            Drawable d;

            d = this.actionBarContainer.getBackground();
            if (d == null) {
                d = new ColorDrawable(UIRes.getColor(c, R.color.decorBackground));
                // Setup background color.
                this.actionBarContainer.setBackground(d);
            }
            d.setAlpha((int) (255.f * scale));

            d = sliverContainer.getBackground();
            if (d == null) {
                d = new ColorDrawable(UIRes.getColor(c, R.color.decorBackground));
                // Setup background color.
                sliverContainer.setBackground(d);
            }
            d.setAlpha((int) (255.f * scale));

            final SliverRefreshLayout srl = (SliverRefreshLayout) sliverContainer;
            final int size = srl.getDecorSize(SliverContainer.SCROLL_LOCATE_HEAD);
            final float ratio;
            ratio = Math.min(size < 0 ? Math.min(offset, 0.f) / (float) size : 0.f, 1.f);
            // Setup view alpha.
            this.actionBarContainer.setAlpha(1.f - ratio);
        }

        private int getDecoratedExtent(@NonNull View decorChild) {
            final ViewGroup.MarginLayoutParams lp;
            lp = (ViewGroup.MarginLayoutParams) decorChild.getLayoutParams();
            return decorChild.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
    }

    private static final class SliverPagerAdapter extends FragmentPagerAdapter {
        @Nullable
        private Fragment currentPrimaryItem;

        public SliverPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position % 2 == 0) {
                return new ChildListFragment();
            }
            return new ChildListFragment2();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @NonNull
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "推荐商品";
            }
            if (position == 1) {
                return "限时折扣";
            }
            if (position == 2) {
                return "加盟品牌";
            }
            if (position == 3) {
                return "精选店铺";
            }
            return String.format("Item %s", position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container,
                                   int position,
                                   @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            this.currentPrimaryItem = (Fragment) object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container,
                                int position,
                                @NonNull Object object) {
            super.destroyItem(container, position, object);
            final Fragment fragment = (Fragment) object;
            if (fragment.equals(this.currentPrimaryItem)) {
                this.currentPrimaryItem = null;
            }
        }

        public void notifyDataSetRefresh() {
            final Fragment f = this.currentPrimaryItem;
            if (f == null) {
                return;
            }
            if (f instanceof UIPageControllerOwner) {
                ((UIPageControllerOwner) f)
                        .getUIPageController()
                        .notifyDataSetRefresh();
            }
        }
    }

    private static final class HorListAdapter extends UIExpandableAdapter<String> {
        @NonNull
        @Override
        public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
            return this.inflate(R.layout.item_child_list_hor_layout);
        }
    }

    private static final class SliverBannerAdapter extends UIExpandableAdapter<String> {
        @NonNull
        @Override
        public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
            return this.inflate(R.layout.item_match_3_layout);
        }

        @Override
        public void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition) {
            final ImageView imageView = holder.requireViewById(R.id.image);
            Glide.with(imageView)
                    .load(this.requireDataBy(groupPosition))
                    .into(imageView);
        }
    }

    private static final class ClassifyHorListAdapter extends UIExpandableAdapter<String> {
        @NonNull
        @Override
        public View onCreateGroupItemView(@NonNull ViewGroup parent, int itemViewType) {
            return this.inflate(R.layout.item_classify_layout);
        }

        @Override
        public void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition) {
            final ImageView imageView = holder.requireViewById(R.id.icon1);
            Glide.with(imageView)
                    .load(_urls[groupPosition % _urls.length])
                    .into(imageView);
        }
    }
}
