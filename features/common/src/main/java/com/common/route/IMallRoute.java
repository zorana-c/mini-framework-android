package com.common.route;

import com.framework.core.route.UIRoute;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface IMallRoute extends UIRoute {
    @NonNull
    static IMallRoute get() {
        return UIRoute.get(IMallRoute.class);
    }
}
