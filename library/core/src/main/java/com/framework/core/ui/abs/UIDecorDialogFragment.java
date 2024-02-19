package com.framework.core.ui.abs;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.navigation.floating.UIDialogFragment;

/**
 * @Author create by Zhengzelong on 2021/12/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIDecorDialogFragment extends UIDialogFragment
        implements UIDecorController.UIComponent {
    @NonNull
    private final UIDecorController
            mUIDecorController = new UIDecorController(this);

    @Override
    @CallSuper
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setStatusBarEnabled(false);
    }

    @NonNull
    @Override
    public UIDecorController getUIPageController() {
        return this.mUIDecorController;
    }
}
