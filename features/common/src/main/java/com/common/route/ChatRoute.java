package com.common.route;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.bean.UIModelInterface;
import com.framework.core.route.UIRoute;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public interface ChatRoute extends UIRoute {
    int TYPE_CHAT = 1;
    int TYPE_GROUP = 2;

    @NonNull
    static ChatRoute get() {
        return UIRoute.get(ChatRoute.class);
    }

    @NonNull
    static Options obtain() {
        return new Options();
    }

    void startChat(@NonNull UIPageControllerOwner owner, @NonNull Options options);

    final class Options implements UIModelInterface {
        private int chatType = TYPE_CHAT;
        @Nullable
        private String chatId;

        /* package */ Options() {
            // nothing
        }

        public int getChatType() {
            return this.chatType;
        }

        @NonNull
        public Options setChatType(int chatType) {
            this.chatType = chatType;
            return this;
        }

        @Nullable
        public String getChatId() {
            return this.chatId;
        }

        @NonNull
        public Options setChatId(@Nullable String chatId) {
            this.chatId = chatId;
            return this;
        }
    }
}
