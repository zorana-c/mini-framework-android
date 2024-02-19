package com.framework.widget.tabs.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.framework.widget.tabs.TabLayout;

/**
 * @Author create by Zhengzelong on 2023-06-28
 * @Email : 171905184@qq.com
 * @Description :
 * <p>
 * {@link com.framework.widget.tabs.TabLayout}
 */
public abstract class FragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter
        implements TabLayout.PagerTitleCallback {

    public FragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public FragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public FragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
