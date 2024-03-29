package com.common.route;

import androidx.annotation.NonNull;

import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface IChatRoute extends UIRoute {
    @NonNull
    static IChatRoute get() {
        return UIRoute.get(IChatRoute.class);
    }

    @NonNull
    static INavigator navigator() {
        return IChatRoute.get().getNavigator();
    }

    @NonNull
    INavigator getNavigator();

    interface INavigator {
        void pushChat(@NonNull UIPageControllerOwner owner);
    }
}
