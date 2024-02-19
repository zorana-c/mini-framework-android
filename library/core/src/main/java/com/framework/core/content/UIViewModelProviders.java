package com.framework.core.content;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIViewModelProviders {

    @NonNull
    public static UIViewModelProvider of(@NonNull UIPageControllerOwner owner) {
        return UIViewModelProviders.of(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static UIViewModelProvider of(@NonNull UIPageController uiPageController) {
        return uiPageController.getUIViewModelProvider();
    }

    @NonNull
    public static UIViewModelProvider ofHost(@NonNull UIPageControllerOwner owner) {
        return UIViewModelProviders.ofHost(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static UIViewModelProvider ofHost(@NonNull UIPageController uiPageController) {
        return UIViewModelProviders.of(uiPageController.requireUIHostController());
    }

    @NonNull
    public static UIViewModelProvider ofParent(@NonNull UIPageControllerOwner owner) {
        return UIViewModelProviders.ofParent(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static UIViewModelProvider ofParent(@NonNull UIPageController uiPageController) {
        return UIViewModelProviders.of(uiPageController.requireUIParentController());
    }
}
