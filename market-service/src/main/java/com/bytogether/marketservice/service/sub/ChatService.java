package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.ChatServiceClient;
import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.ChatRoomResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponseWrap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatServiceClient chatServiceClient;

    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {

        ChatRoomResponse chatRoom = chatServiceClient.createChatRoom(chatRoomCreateRequest);

        return chatRoom;
    }

    public ParticipantListResponse getParticipants(Long marketId) {

        ParticipantListResponse participants = chatServiceClient.getParticipants(marketId);

        return participants;
    }

    public List<ParticipantListResponseWrap> getParticipantsWrap(List<Long> marketId) {

        List<ParticipantListResponseWrap> participantListResponseWraps = new ArrayList<>();

        marketId.forEach(id -> {
            ParticipantListResponseWrap participantListResponseWrap = new ParticipantListResponseWrap();
            ParticipantListResponse participants = chatServiceClient.getParticipants(id);
            participantListResponseWrap.setRequestMarketId(id);
            participantListResponseWrap.setParticipantListResponse(participants);
            participantListResponseWraps.add(participantListResponseWrap);
        });
        return participantListResponseWraps;
    }
}
