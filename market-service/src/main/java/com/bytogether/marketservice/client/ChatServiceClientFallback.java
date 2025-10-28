package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.ChatRoomResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponseWrap;
import com.bytogether.marketservice.client.dto.response.UserMarketIdsResponse;
import com.bytogether.marketservice.exception.MarketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ChatServiceClientFallback implements ChatServiceClient {
    @Override
    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest request) {
        log.error("============= Chat Service is unavailable. Fallback triggered for createChatRoom =============");
        log.error("Chat Service is unavailable. Fallback triggered for createChatRoom with request: {}", request);
        throw new MarketException("Chat Service is currently unavailable. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ParticipantListResponse getParticipants(Long marketId) {
        // fallback 로직
        ParticipantListResponse response = new ParticipantListResponse();
        response.setRoomId(null);
        response.setCurrentParticipants(null);
        response.setCurrentBuyers(null);
        response.setParticipants(Collections.emptyList());
        return response;
    }

    @Override
    public UserMarketIdsResponse getUserChatRooms(Long userId) {
        // fallback 로직
        UserMarketIdsResponse response = new UserMarketIdsResponse();
        response.setOngoing(Collections.emptyList());
        response.setCompleted(Collections.emptyList());
        response.setOngoingCount(0);
        response.setCompletedCount(0);
        return response;
    }

    @Override
    public List<ParticipantListResponseWrap> getParticipantList(List<Long> marketIds) {
        // fallback 로직

        return Collections.emptyList();
    }
}
