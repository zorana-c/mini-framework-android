package com.framework.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.framework.core.compat.UIRes;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.framework.core.ui.abs.UIDecorFragment;
import com.framework.core.widget.UIDecorLayout;
import com.framework.demo.widget.CustomRefreshLoadView;
import com.framework.widget.compat.UIViewCompat;
import com.framework.widget.sliver.SliverContainer;
import com.framework.widget.sliver.SliverRefreshLayout;

/**
 * @Author create by Zhengzelong on 2023-07-06
 * @Email : 171905184@qq.com
 * @Description :
 */
public class SliverFragment3 extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_sliver_3;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIDecorController dc = this.getUIPageController();
        dc.fitsParentLayouts(UIDecorLayout.DECOR_CONTENT);
        dc.layoutContent();

        final UIActionBarController abc = dc.getUIActionBarController();
        abc.setBackgroundColor(Color.TRANSPARENT);
        abc.setMenuResource(R.mipmap.ic_message_hint);

        final Context c = this.requireContext();
        int heightUsed = 0;
        heightUsed += UIViewCompat.getStatusBarHeight(c);
        heightUsed += UIRes.getDimensionPixelSize(c, R.dimen.ui_action_bar_height);

        SliverRefreshLayout srl;
        srl = this.requireViewById(R.id.hRefreshLayout);
        srl.setHeadLoadView(new CustomRefreshLoadView(c));
        srl.setRefreshCallback((sliverRefreshLayout, locate) -> {
            sliverRefreshLayout.postDelayed(() -> {
                sliverRefreshLayout.completeRefreshed(820L);
            }, 2500);
        });
        final SliverContainer.LayoutParams lp;
        lp = (SliverContainer.LayoutParams) srl.getLayoutParams();
        lp.heightUsed = heightUsed;
        srl.forcedRefreshing(0);

        srl = this.requireViewById(R.id.tRefreshLayout);
        srl.setRefreshCallback((sliverRefreshLayout, locate) -> {
            sliverRefreshLayout.postDelayed(() -> {
                sliverRefreshLayout.completeRefreshed(420L);
            }, 2500);
        });

        final ImageView iv;
        iv = this.requireViewById(R.id.uiDecorToolsBackIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setImageTintList(UIRes.getColorStateList(c, android.R.color.white));
        }
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
