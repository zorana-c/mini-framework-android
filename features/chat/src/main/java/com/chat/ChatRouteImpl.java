package com.chat;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chat.ui.ChatFragment;
import com.common.route.IChatRoute;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.route.UIRoute;
import com.google.auto.service.AutoService;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
@AutoService(UIRoute.class)
public class ChatRouteImpl implements IChatRoute {
    @NonNull
    private final INavigator mNavigator = new NavigatorImpl();

    @Override
    public void init(@NonNull Context context) {
        ChatInit.init(context);
    }

    @NonNull
    @Override
    public INavigator getNavigator() {
        return this.mNavigator;
    }

    private static final class NavigatorImpl implements INavigator {
        @Override
        public void pushChat(@NonNull UIPageControllerOwner owner) {
            owner.getUIPageController()
                    .getUINavigatorController()
                    .startFragment(ChatFragment.class);
        }
    }
}
