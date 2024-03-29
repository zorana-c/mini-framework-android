package com.chat.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.route.IChatRoute;
import com.framework.core.content.UIActionBarController;
import com.framework.core.ui.abs.UIDecorFragment;
import com.navigation.UINavigatorController;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description : 即时聊天通讯
 */
@UINavigatorController.FragmentRoute(
        hostClass = ChatAbility.class,
        launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
)
public class ChatFragment extends UIDecorFragment {
    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return 0;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setTitleText("Im-Chat");

        uiActionBarController.setMenuText("Chat");
        uiActionBarController.setMenuClickListener(view -> {
            IChatRoute.navigator().pushChat(this);
        });
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
    }
}
