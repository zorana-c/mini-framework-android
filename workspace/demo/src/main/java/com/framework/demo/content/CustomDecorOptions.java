package com.framework.demo.content;

import androidx.annotation.NonNull;

import com.framework.core.compat.UILog;
import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIDecorOptions;
import com.framework.core.content.UIPageController;
import com.framework.core.widget.UIDecorLayout;

/**
 * @Author create by Zhengzelong on 2022-11-23
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CustomDecorOptions extends UIDecorOptions {
    @Override
    public void apply(@NonNull UIPageController uiPageController) {
        super.apply(uiPageController);
        UILog.e("=========== Apply UIDemoOptions");
    }

    @Override
    public void applyException(@NonNull UIDecorController uiDecorController,
                               @NonNull Throwable throwable) {
        super.applyException(uiDecorController, throwable);
        UILog.e("=========== Apply Exception: " + throwable.getMessage());
    }

    @Override
    protected void applyDecorOptions(@NonNull UIDecorController uiDecorController) {
        super.applyDecorOptions(uiDecorController);
        final UIDecorLayout uiDecorLayout = uiDecorController.getUIDecorLayout();
//        uiDecorLayout.setEnterAnimation(R.anim.slide_in_from_right);
//        uiDecorLayout.setExitAnimation(R.anim.slide_out_to_left);
    }
}
