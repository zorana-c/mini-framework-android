package com.framework.widget.tabs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.framework.widget.sliver.SliverViewPager2;

import java.lang.ref.WeakReference;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING;

/**
 * @Author create by Zhengzelong on 2023-06-28
 * @Email : 171905184@qq.com
 * @Description : 兼容 ViewPager2
 * <p>
 * {@link com.google.android.material.tabs.TabLayout}
 */
public class TabLayout extends com.google.android.material.tabs.TabLayout {
    @Nullable
    private ViewPager2 mViewPager;
    @Nullable
    private RecyclerView.Adapter<?> mAdapter;
    @Nullable
    private ComponentListener mComponentListener;
    @Nullable
    private AdapterDataObserver mAdapterDataObserver;
    @Nullable
    private OnPageChangeCallback mOnPageChangeCallback;
    private boolean mIsSetupViewPagerImplicitly;

    public TabLayout(@NonNull Context context) {
        this(context, null);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mIsSetupViewPagerImplicitly) {
            // If we've been setup with a ViewPager implicitly,
            // let's clear out any listeners, etc.
            this.setupWithViewPager((ViewPager2) null);
            this.mIsSetupViewPagerImplicitly = false;
        }
    }

    /**
     * {@link com.framework.widget.tabs.adapter.FragmentStateAdapter}
     *
     * @deprecated
     */
    public void setTabsFromPagerAdapter(@Nullable RecyclerView.Adapter<?> adapter) {
        this.setPagerAdapter(adapter, false);
    }

    /**
     * {@link com.framework.widget.tabs.adapter.FragmentStateAdapter}
     */
    public void setupWithViewPager(@Nullable SliverViewPager2 viewPager) {
        this.setupWithViewPager(viewPager, true);
    }

    /**
     * {@link com.framework.widget.tabs.adapter.FragmentStateAdapter}
     */
    public void setupWithViewPager(@Nullable SliverViewPager2 viewPager, boolean autoRefresh) {
        if (viewPager == null) {
            this.setupWithViewPager((ViewPager2) null, autoRefresh, false);
        } else {
            this.setupWithViewPager(viewPager.getViewPager(), autoRefresh, false);
        }
    }

    /**
     * {@link com.framework.widget.tabs.adapter.FragmentStateAdapter}
     */
    public void setupWithViewPager(@Nullable ViewPager2 viewPager) {
        this.setupWithViewPager(viewPager, true);
    }

    /**
     * {@link com.framework.widget.tabs.adapter.FragmentStateAdapter}
     */
    public void setupWithViewPager(@Nullable ViewPager2 viewPager, boolean autoRefresh) {
        this.setupWithViewPager(viewPager, autoRefresh, false);
    }

    private void setupWithViewPager(@Nullable ViewPager2 viewPager, boolean autoRefresh, boolean implicitSetup) {
        final ViewPager2 oldViewPager = this.mViewPager;
        if (oldViewPager == viewPager) {
            return;
        }
        if (this.mOnPageChangeCallback != null) {
            if (oldViewPager != null) {
                oldViewPager.unregisterOnPageChangeCallback(this.mOnPageChangeCallback);
            }
        }
        if (this.mComponentListener != null) {
            this.removeOnTabSelectedListener(this.mComponentListener);
        }
        this.mViewPager = viewPager;
        if (viewPager == null) {
            this.setPagerAdapter(null, false);
        } else {
            if (this.mComponentListener == null) {
                this.mComponentListener = new ComponentListener();
            }
            this.addOnTabSelectedListener(this.mComponentListener);

            if (this.mOnPageChangeCallback == null) {
                this.mOnPageChangeCallback = new OnPageChangeCallback(this);
            }
            this.mOnPageChangeCallback.reset();
            viewPager.registerOnPageChangeCallback(this.mOnPageChangeCallback);

            final RecyclerView.Adapter<?> adapter = viewPager.getAdapter();
            if (adapter != null) {
                this.setPagerAdapter(adapter, autoRefresh);
            } else {
                throw new IllegalStateException("Not adapter set");
            }
            this.setScrollPosition(viewPager.getCurrentItem(), 0f, true);
        }
        this.mIsSetupViewPagerImplicitly = implicitSetup;
    }

    private void setPagerAdapter(@Nullable RecyclerView.Adapter<?> adapter, boolean addObserver) {
        final RecyclerView.Adapter<?> oldAdapter = this.mAdapter;
        if (this.mAdapterDataObserver != null) {
            if (oldAdapter != null) {
                oldAdapter.unregisterAdapterDataObserver(this.mAdapterDataObserver);
            }
        }
        this.mAdapter = adapter;
        if (addObserver && adapter != null) {
            if (this.mAdapterDataObserver == null) {
                this.mAdapterDataObserver = new AdapterDataObserver(this);
            }
            adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
        }
        this.populateFromPagerAdapter();
    }

    private void populateFromPagerAdapter() {
        this.removeAllTabs();

        final RecyclerView.Adapter<?> adapter = this.mAdapter;
        if (adapter == null) {
            return;
        }
        final int itemCount = adapter.getItemCount();
        for (int position = 0; position < itemCount; position++) {
            CharSequence titleText = null;

            if (adapter instanceof PagerTitleCallback) {
                final PagerTitleCallback callback;
                callback = (PagerTitleCallback) adapter;
                titleText = callback.getPageTitle(position);
            }
            this.addTab(this.newTab().setText(titleText), false);
        }

        final ViewPager2 viewPager = this.mViewPager;
        if (viewPager == null) {
            return;
        }
        final int curPosition = viewPager.getCurrentItem();
        final int selPosition = this.getSelectedTabPosition();
        if (itemCount > 0
                && curPosition != selPosition
                && curPosition < this.getTabCount()) {
            this.selectTab(this.getTabAt(curPosition));
        }
    }

    private void setCurrentItem(int position) {
        final ViewPager2 viewPager = this.mViewPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }

    private final class ComponentListener implements OnTabSelectedListener {
        @Override
        public void onTabSelected(@NonNull Tab tab) {
            setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(@NonNull Tab tab) {
            // no-op
        }

        @Override
        public void onTabReselected(@NonNull Tab tab) {
            // no-op
        }
    }

    private static final class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @NonNull
        private final WeakReference<TabLayout> mTabLayoutRef;

        public AdapterDataObserver(@NonNull TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onChanged() {
            this.populateFromPagerAdapter();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            this.populateFromPagerAdapter();
        }

        private void populateFromPagerAdapter() {
            final TabLayout tabLayout = this.mTabLayoutRef.get();
            if (tabLayout != null) {
                tabLayout.populateFromPagerAdapter();
            }
        }
    }

    private static final class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @NonNull
        private final WeakReference<TabLayout> mTabLayoutRef;
        @ViewPager2.ScrollState
        private int mScrollState;
        @ViewPager2.ScrollState
        private int mPreviousScrollState;

        public OnPageChangeCallback(@NonNull TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int scrollState) {
            this.mPreviousScrollState = this.mScrollState;
            this.mScrollState = scrollState;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final TabLayout tabLayout = this.mTabLayoutRef.get();
            if (tabLayout == null) {
                return;
            }
            // Only update the text selection if we're not settling, or we are settling after
            // being dragged.
            final boolean updateText = this.mScrollState != SCROLL_STATE_SETTLING
                    || this.mPreviousScrollState == SCROLL_STATE_DRAGGING;
            // Update the indicator if we're not settling after being idle. This is caused
            // from a setCurrentItem() call and will be handled by an animation from
            // onPageSelected() instead.
            final boolean updateIndicator = !(this.mScrollState == SCROLL_STATE_SETTLING
                    && this.mPreviousScrollState == SCROLL_STATE_IDLE);
            tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
        }

        @Override
        public void onPageSelected(int position) {
            final TabLayout tabLayout = this.mTabLayoutRef.get();
            if (tabLayout == null) {
                return;
            }
            final int tabItemCount = tabLayout.getTabCount();
            final int selPosition = tabLayout.getSelectedTabPosition();
            // Select the tab, only updating the indicator if we're not being dragged/settled
            // (since onPageScrolled will handle that).
            final boolean updateIndicator = this.mScrollState == SCROLL_STATE_IDLE
                    || (this.mScrollState == SCROLL_STATE_SETTLING && this.mPreviousScrollState == SCROLL_STATE_IDLE);
            if (position != selPosition
                    && position < tabItemCount) {
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }
        }

        private void reset() {
            this.mScrollState = SCROLL_STATE_IDLE;
            this.mPreviousScrollState = SCROLL_STATE_IDLE;
        }
    }

    public interface PagerTitleCallback {

        @Nullable
        CharSequence getPageTitle(int position);
    }
}
