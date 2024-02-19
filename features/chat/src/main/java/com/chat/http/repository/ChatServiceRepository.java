package com.chat.http.repository;

import androidx.annotation.NonNull;

import com.chat.http.service.ChatService;
import com.common.http.repository.AbsServiceRepository;
import com.framework.core.http.HttpServiceRepository;

/**
 * @Author create by Zhengzelong on 2024-01-10
 * @Email : 171905184@qq.com
 * @Description :
 */
public class ChatServiceRepository extends AbsServiceRepository {
    @NonNull
    public static ChatServiceRepository get() {
        return HttpServiceRepository.get(ChatServiceRepository.class);
    }

    @Autowired(tag = "chatService")
    private ChatService chatService;
}
