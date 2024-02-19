package com.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.chat.constant.Constants;
import com.chat.ui.ChatFragment;
import com.common.route.ChatRoute;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class ChatRouteImpl implements ChatRoute {
    @Override
    public void init(@NonNull Context context) {
        ChatInit.init(context);
    }

    @Override
    public void startChat(@NonNull UIPageControllerOwner owner, @NonNull Options options) {
        final Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_PARAM, options);

        owner.getUIPageController()
                .getUINavigatorController()
                .startFragment(ChatFragment.class, args);
    }
}
