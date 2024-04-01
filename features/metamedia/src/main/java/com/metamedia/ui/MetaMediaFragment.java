package com.metamedia.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.common.route.IAppRoute;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIFragment;
import com.google.android.material.tabs.TabLayout;
import com.metamedia.R;
import com.metamedia.ui.adapter.MetaMediaPagerAdapter;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MetaMediaFragment extends UIFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_metamedia;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        RxView.of(this)
                .click(this::onClick, R.id.functs);

        final ViewPager viewPager;
        viewPager = this.findViewById(R.id.pageContent);
        viewPager.setAdapter(new MetaMediaPagerAdapter(this));
        final TabLayout tabLayout;
        tabLayout = this.findViewById(R.id.pageTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.postOnAnimation(() -> {
            final int index = tabLayout.getTabCount() - 1;
            tabLayout.selectTab(tabLayout.getTabAt(index));
        });
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }

    private void onClick(@NonNull View view) {
        final int id = view.getId();
        if (R.id.functs == id) {
            IAppRoute
                    .get()
                    .getDrawerController(this)
                    .openSimpleComponent();
        }
    }
}
