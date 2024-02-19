package com.framework.core.content;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIPageControllerOwner {

    @NonNull
    <T extends UIPageController> T getUIPageController();
}
