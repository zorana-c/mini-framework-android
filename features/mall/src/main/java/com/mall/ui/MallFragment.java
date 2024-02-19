package com.mall.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.framework.core.compat.UIRes;
import com.framework.core.content.UIActionBarController;
import com.framework.core.ui.abs.UIDecorFragment;
import com.mall.R;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MallFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mall;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setBackEnabled(false);
        uiActionBarController.setBackgroundColor(Color.TRANSPARENT);

        View view;
        view = this.getView();
        view.setBackgroundColor(UIRes.getColor(R.color.colorBackground));
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
