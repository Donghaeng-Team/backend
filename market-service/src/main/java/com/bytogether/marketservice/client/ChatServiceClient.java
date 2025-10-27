package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.ChatRoomResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponse;
import com.bytogether.marketservice.client.dto.response.UserMarketIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@FeignClient(name = "chat-service",
        url = "${openfeign.chat-service.url}",
        fallback = ChatServiceClientFallback.class,
        configuration = ServiceFeignConfig.class
)
public interface ChatServiceClient {

    @PostMapping("/internal/v1/chat/create")
    ChatRoomResponse createChatRoom(@RequestBody ChatRoomCreateRequest request);

    @GetMapping("/internal/v1/chat/{marketId}/participants")
    ParticipantListResponse getParticipants(@PathVariable("marketId") Long marketId);

    @GetMapping("/internal/v1/chat/mylist/{userId}")
    UserMarketIdsResponse getUserChatRooms(@PathVariable("userId") Long userId);

}
