package com.metamedia.ui.publish;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.framework.core.content.UIActionBarController;
import com.framework.core.ui.abs.UIDecorFragment;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public class PublishFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setBackEnabled(false);
        uiActionBarController.setTitleText("Publish");
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
