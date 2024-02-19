package com.common.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.framework.core.content.UIActionBarController;
import com.framework.core.ui.abs.UIDecorFragment;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class PlaceholderFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setEnabled(false);
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
