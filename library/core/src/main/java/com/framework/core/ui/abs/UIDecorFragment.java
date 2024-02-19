package com.framework.core.ui.abs;

import androidx.annotation.NonNull;

import com.framework.core.content.UIDecorController;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public abstract class UIDecorFragment extends UIFragment
        implements UIDecorController.UIComponent {
    @NonNull
    private final UIDecorController
            mUIDecorController = new UIDecorController(this);

    @NonNull
    @Override
    public UIDecorController getUIPageController() {
        return this.mUIDecorController;
    }
}
