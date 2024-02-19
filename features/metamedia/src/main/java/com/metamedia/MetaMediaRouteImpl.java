package com.metamedia;

import android.content.Context;

import androidx.annotation.NonNull;

import com.common.route.MetaMediaRoute;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class MetaMediaRouteImpl implements MetaMediaRoute {
    @Override
    public void init(@NonNull Context context) {
        MetaMediaInit.init(context);
    }
}
