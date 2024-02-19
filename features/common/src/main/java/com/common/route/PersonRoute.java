package com.common.route;

import com.framework.core.route.UIRoute;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface PersonRoute extends UIRoute {
    @NonNull
    static PersonRoute get() {
        return UIRoute.get(PersonRoute.class);
    }
}
