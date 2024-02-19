package com.common.route;

import androidx.annotation.NonNull;

import com.framework.core.route.UIRoute;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface MetaMediaRoute extends UIRoute {
    @NonNull
    static MetaMediaRoute get() {
        return UIRoute.get(MetaMediaRoute.class);
    }
}
