package com.guide.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.framework.core.UIFramework;
import com.framework.core.bean.UIModelInterface;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIFragment;
import com.guide.R;
import com.guide.bean.GuideStatus;
import com.guide.ui.adapter.GuidePagerAdapter;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class GuideFragment extends UIFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_guide;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        RxView.of(this)
                .click(widget -> this.finish(), R.id.text);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        final Bundle args = this.requireArguments();
        final int[] data = args.getIntArray("Data");
        if (data == null) {
            throw new IllegalStateException("Parameter abnormality");
        }
        final ViewPager2 viewPager;
        viewPager = this.requireViewById(R.id.pageContent);
        viewPager.setOffscreenPageLimit(data.length);
        GuidePagerAdapter adapter;
        adapter = (GuidePagerAdapter) viewPager.getAdapter();
        if (adapter == null) {
            adapter = new GuidePagerAdapter();

            final ComponentListener componentListener;
            componentListener = new ComponentListener(adapter);
            viewPager.registerOnPageChangeCallback(componentListener);
            viewPager.setAdapter(adapter);
        }
        adapter.setDataSources(data);
    }

    private void finish() {
        GuideStatus guideStatus;
        guideStatus = UIModelInterface.get(GuideStatus.class);
        if (guideStatus == null) {
            guideStatus = new GuideStatus();
        }
        guideStatus.setVersionCode(UIFramework.getVersionCode());
        guideStatus.saveToShared();
        this.getUINavigatorController()
                .setResult(Activity.RESULT_OK)
                .navigateUp();
    }

    private void setLaunchMain(boolean launchEnabled) {
        final View view;
        view = this.requireViewById(R.id.text);
        view.setVisibility(launchEnabled ? View.VISIBLE : View.GONE);
    }

    final class ComponentListener extends ViewPager2.OnPageChangeCallback {
        @NonNull
        private final GuidePagerAdapter adapter;

        public ComponentListener(@NonNull GuidePagerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onPageSelected(int position) {
            final int itemCount = this.adapter.getItemCount();
            GuideFragment.this.setLaunchMain(position >= itemCount - 1);
        }
    }
}
