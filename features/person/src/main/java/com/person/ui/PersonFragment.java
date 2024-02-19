package com.person.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.common.route.AppRoute;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIDecorOptions;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIDecorFragment;
import com.framework.core.widget.UIDecorLayout;
import com.framework.widget.sliver.SliverContainer;
import com.google.android.material.tabs.TabLayout;
import com.person.R;
import com.person.ui.adapter.PersonPagerAdapter;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PersonFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_person;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setBackEnabled(false);
        uiActionBarController.setBackgroundColor(Color.WHITE);
        uiActionBarController.setBackgroundAlpha(0.f);
        uiActionBarController.setToolsBar(R.layout.toolsbar_person_layout);

        final UIDecorController uiDecorController;
        uiDecorController = this.getUIPageController();
        uiDecorController.layoutLoading();
        uiDecorController.fitsParentLayouts(UIDecorLayout.DECOR_CONTENT);

        final SliverContainer sliverContainer = this.requireViewById(R.id.sliverContainer);
        sliverContainer.addOnScrollListener(new SliverContainer.SimpleOnScrollListener() {
            @Override
            public void onScrolled(@NonNull SliverContainer sliverContainer, int dx, int dy) {
                final float scrollRange = sliverContainer.computeVerticalScrollRange();
                final float scrollExtent = sliverContainer.computeVerticalScrollExtent();
                final float scrollOffset = sliverContainer.computeVerticalScrollOffset();
                setupScrollScale(Math.max(0.f, Math.min(scrollOffset / (scrollRange - scrollExtent), 1.f)));
            }
        });

        RxView.of(this)
                .click(this::onClick, R.id.functs);

        final ViewPager viewPager;
        viewPager = this.requireViewById(R.id.pageContent);
        viewPager.setAdapter(new PersonPagerAdapter(this));
        final TabLayout tabLayout;
        tabLayout = this.requireViewById(R.id.pageTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        final ViewGroup viewPagerContainer = this.requireViewById(R.id.pageContainer);
        final SliverContainer.LayoutParams layoutParams;
        layoutParams = (SliverContainer.LayoutParams) viewPagerContainer.getLayoutParams();
        layoutParams.heightUsed = this.getUIActionBarController().getHeight();
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        this.getUIPageController().postContentOnAnimation(UIDecorOptions.MS_ANIM);
    }

    private void setupScrollScale(float scrollScale) {
        this.getUIActionBarController().setBackgroundAlpha(scrollScale);
    }

    private void onClick(@NonNull View view) {
        final int id = view.getId();
        if (R.id.functs == id) {
            AppRoute.get()
                    .getDrawerController(this)
                    .openSubtleComponent();
        }
    }
}
