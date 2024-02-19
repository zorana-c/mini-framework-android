package com.chat.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.chat.constant.Constants;
import com.common.route.ChatRoute;
import com.framework.core.compat.UIToast;
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
            ChatRoute.get().startChat(this, ChatRoute.obtain()
                    .setChatId(String.valueOf(System.currentTimeMillis()))
                    .setChatType(ChatRoute.TYPE_CHAT));
        });
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        final Bundle args = this.requireArguments();
        final ChatRoute.Options options;
        options = (ChatRoute.Options) args.getSerializable(Constants.KEY_PARAM);
        UIToast.asyncToast(options.getChatId());
    }
}
