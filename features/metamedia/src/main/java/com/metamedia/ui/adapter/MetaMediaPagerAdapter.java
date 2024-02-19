package com.metamedia.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.framework.core.content.UIPageControllerOwner;
import com.metamedia.constant.Constants;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MetaMediaPagerAdapter extends FragmentPagerAdapter {
    @NonNull
    private final FragmentManager fragmentManager;

    public MetaMediaPagerAdapter(@NonNull UIPageControllerOwner owner) {
        this(owner.getUIPageController().getChildFragmentManager());
    }

    public MetaMediaPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        final Class<? extends Fragment> tClass;
        tClass = Constants.FRAGMENTS[position];
        return this.fragmentManager.getFragmentFactory()
                .instantiate(tClass.getClassLoader(), tClass.getName());
    }

    @Override
    public int getCount() {
        return Constants.FRAGMENTS.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.TITLES[position];
    }
}
