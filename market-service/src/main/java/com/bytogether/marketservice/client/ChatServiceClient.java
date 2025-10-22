package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.ChatRoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Chat Service와 통신하기 위한 Feign Client
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-22
 *
 */

@FeignClient(name = "chat-service",url = "${openfeign.chat-service.url}")
public interface ChatServiceClient {

    @PostMapping("/internal/v1/chat/create")
    ChatRoomResponse createChatRoom(@RequestBody ChatRoomCreateRequest request);

}
