package com.person;

import android.content.Context;

import androidx.annotation.NonNull;

import com.common.route.IPersonRoute;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-29
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class PersonRouteImpl implements IPersonRoute {
    @Override
    public void init(@NonNull Context context) {
        PersonInit.init(context);
    }
}
