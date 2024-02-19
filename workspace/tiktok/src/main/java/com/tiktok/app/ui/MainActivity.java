package com.tiktok.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.common.util.AppUtils;
import com.framework.core.compat.UIToast;
import com.framework.core.ui.abs.UIFragmentActivity;
import com.google.android.material.tabs.TabLayout;
import com.navigation.UINavigatorController;
import com.person.ui.function.SimpleFragment;
import com.person.ui.function.SubtleFragment;
import com.tiktok.app.R;
import com.tiktok.app.constant.Constants;
import com.tiktok.app.ui.adapter.MainPagerAdapter;

/**
 * @Author create by Zhengzelong on 2024-01-19
 * @Email : 171905184@qq.com
 * @Description : 程序主界面
 */
@UINavigatorController.ActivityRoute(
        launchFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP
)
public class MainActivity extends UIFragmentActivity {
    @Nullable
    private MainController mainController;
    private long lastTimeMillis = 0;

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final ViewPager viewPager;
        viewPager = this.findViewById(R.id.pageContent);
        viewPager.setAdapter(new MainPagerAdapter(this));
        final TabLayout tabLayout;
        tabLayout = this.findViewById(R.id.pageTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        this.mainController = MainController.with(this);
        this.mainController.setSimpleComponent(SimpleFragment.class);
        this.mainController.setSubtleComponent(SubtleFragment.class);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        // nothing
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        final Bundle args = this.getUIPageController().getArguments();
        final int position = args.getInt(Constants.KEY_POSITION);
        if (position == -1) {
            return;
        }
        this.setCurrentItem(position);
    }

    @NonNull
    public MainController getMainController() {
        if (this.mainController == null) {
            throw new IllegalStateException("ERROR");
        }
        return this.mainController;
    }

    private void setCurrentItem(int position) {
        final TabLayout tabLayout;
        tabLayout = this.findViewById(R.id.pageTabLayout);
        if (tabLayout == null) {
            return;
        }
        final TabLayout.Tab layoutTab;
        layoutTab = tabLayout.getTabAt(position);
        if (layoutTab == null) {
            return;
        }
        layoutTab.select();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (this.mainController != null
                    && this.mainController.goBack()) {
                return true;
            }
            final long tTimeMillis = Constants.BACKED_TIME * 1000;
            final long cTimeMillis = System.currentTimeMillis();
            if ((cTimeMillis - this.lastTimeMillis) >= tTimeMillis) {
                this.lastTimeMillis = cTimeMillis;
                UIToast.toast("再按一次退出程序");
            } else {
                // 结束界面
                this.getUINavigatorController().navigateUp();
                // 结束程序
                AppUtils.exitApplication();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
