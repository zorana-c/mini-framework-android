package com.mall;

import android.content.Context;

import androidx.annotation.NonNull;

import com.common.route.MallRoute;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-30
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class MallRouteImpl implements MallRoute {
    @Override
    public void init(@NonNull Context context) {
        MallInit.init(context);
    }
}
