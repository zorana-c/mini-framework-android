package com.common.route;

import androidx.annotation.NonNull;

import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface GuideRoute extends UIRoute {
    @NonNull
    static GuideRoute get() {
        return UIRoute.get(GuideRoute.class);
    }

    boolean startGuide(@NonNull UIPageControllerOwner owner, @NonNull int[] resIds);
}
