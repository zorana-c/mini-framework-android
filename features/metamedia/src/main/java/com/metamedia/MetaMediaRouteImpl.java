package com.metamedia;

import android.content.Context;

import androidx.annotation.NonNull;

import com.common.route.IMetaMediaRoute;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class MetaMediaRouteImpl implements IMetaMediaRoute {
    @Override
    public void init(@NonNull Context context) {
        MetaMediaInit.init(context);
    }
}
