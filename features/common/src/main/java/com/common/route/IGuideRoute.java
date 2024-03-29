package com.common.route;

import androidx.annotation.NonNull;

import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface IGuideRoute extends UIRoute {
    @NonNull
    static IGuideRoute get() {
        return UIRoute.get(IGuideRoute.class);
    }

    @NonNull
    static INavigator navigator() {
        return IGuideRoute.get().getNavigator();
    }

    @NonNull
    INavigator getNavigator();

    interface INavigator {
        boolean pushGuide(@NonNull UIPageControllerOwner owner, @NonNull int[] resIds);
    }
}
