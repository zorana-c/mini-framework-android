package com.framework.core.content;

import androidx.annotation.NonNull;

/**
 * @Author create by Zhengzelong on 2022/6/22
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface UIActivityDispatcherOwner {

    @NonNull
    UIActivityDispatcher getUIActivityDispatcher();
}
