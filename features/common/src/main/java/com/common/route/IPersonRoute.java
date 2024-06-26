package com.common.route;

import com.framework.core.route.UIRoute;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface IPersonRoute extends UIRoute {
    @NonNull
    static IPersonRoute get() {
        return UIRoute.get(IPersonRoute.class);
    }
}
