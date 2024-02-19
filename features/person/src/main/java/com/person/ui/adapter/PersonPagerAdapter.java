package com.person.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.framework.core.content.UIPageControllerOwner;
import com.person.constant.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PersonPagerAdapter extends FragmentPagerAdapter {
    @NonNull
    private final FragmentManager fragmentManager;

    public PersonPagerAdapter(@NonNull UIPageControllerOwner owner) {
        this(owner.getUIPageController().getChildFragmentManager());
    }

    public PersonPagerAdapter(@NonNull FragmentManager fragmentManager) {
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

    // Inner method.
    @NonNull
    private final List<Fragment> fragments = new ArrayList<>();
    @Nullable
    private ComponentListener componentListener;

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
        if (this.componentListener == null) {
            this.componentListener = new ComponentListener();

            final ViewPager viewPager = (ViewPager) container;
            viewPager.addOnAdapterChangeListener(this.componentListener);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,
                            @NonNull Object object) {
        super.destroyItem(container, position, object);
        final Fragment fragment = (Fragment) object;
        if (this.fragments.contains(fragment)) {
            return;
        }
        this.fragments.add(fragment);
    }

    private void destroyFragments(@NonNull ViewPager viewPager) {
        if (this.componentListener != null) {
            viewPager.removeOnAdapterChangeListener(this.componentListener);
            this.componentListener = null;
        }
        final List<Fragment> fragments = this.fragments;
        if (!fragments.isEmpty()) {
            final FragmentTransaction fragmentTransaction;
            fragmentTransaction = this.fragmentManager.beginTransaction();
            for (Fragment fragment : fragments) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.commitNow();
        }
        fragments.clear();
    }

    private static final class ComponentListener
            implements ViewPager.OnAdapterChangeListener {
        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            if (oldAdapter instanceof PersonPagerAdapter) {
                ((PersonPagerAdapter) oldAdapter).destroyFragments(viewPager);
            }
        }
    }
}
