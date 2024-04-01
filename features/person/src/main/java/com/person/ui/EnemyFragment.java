package com.person.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.common.route.IAppRoute;
import com.framework.core.compat.UILog;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIPageController;
import com.framework.core.ui.abs.UIDecorFragment;
import com.framework.core.widget.UIDecorLayout;
import com.framework.widget.sliver.SliverContainer;
import com.google.android.material.tabs.TabLayout;
import com.person.R;
import com.person.constant.Constants;
import com.person.ui.adapter.PersonPagerAdapter;

/**
 * @Author create by Zhengzelong on 2024-02-01
 * @Email : 171905184@qq.com
 * @Description :
 */
public class EnemyFragment extends UIDecorFragment
        implements IAppRoute.IDrawerCallback {
    @NonNull
    public static Bundle asBundle(@NonNull String personId) {
        final Bundle args = new Bundle();
        args.putString(Constants.KEY_PERSON_ID, personId);
        return args;
    }

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_enemy;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setBackgroundColor(Color.WHITE);
        uiActionBarController.setBackgroundAlpha(0.f);
        uiActionBarController.setBackClickListener(widget -> {
            IAppRoute
                    .get()
                    .getDrawerController(this)
                    .closeDrawerComponent(this);
        });

        final UIDecorController uiDecorController;
        uiDecorController = this.getUIPageController();
        uiDecorController.layoutContent();
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

        final ViewPager viewPager;
        viewPager = this.requireViewById(R.id.pageContent);
        final TabLayout tabLayout;
        tabLayout = this.requireViewById(R.id.pageTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.removeAllTabs();

        final ViewGroup viewPagerContainer = this.requireViewById(R.id.pageContainer);
        final SliverContainer.LayoutParams layoutParams;
        layoutParams = (SliverContainer.LayoutParams) viewPagerContainer.getLayoutParams();
        layoutParams.heightUsed = this.getUIActionBarController().getHeight();
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        final Bundle args = this.requireArguments();
        final String personId = args.getString(Constants.KEY_PERSON_ID);
        UILog.e("PersonId: " + personId);

        final ViewPager viewPager;
        viewPager = this.requireViewById(R.id.pageContent);
        viewPager.setAdapter(new PersonPagerAdapter(this));
    }

    private void setupScrollScale(float scrollScale) {
        this.getUIActionBarController().setBackgroundAlpha(scrollScale);
    }

    @Override
    public void onNewArguments(@NonNull Bundle arguments) {
        // Resets arguments.
        this.setArguments(arguments);
        // Resets refreshed.
        final ViewPager viewPager;
        viewPager = this.requireViewById(R.id.pageContent);
        viewPager.setAdapter(null);

        final SliverContainer sliverContainer;
        sliverContainer = this.requireViewById(R.id.sliverContainer);
        sliverContainer.scrollTo(0, 0);

        final UIPageController uiPageController;
        uiPageController = this.getUIPageController();
        uiPageController.notifyDataSetRefresh();
        // After see onUIRefresh.
    }
}
