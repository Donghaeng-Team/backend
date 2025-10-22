package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.ChatServiceClient;
import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatServiceClient chatServiceClient;

    public ChatRoomResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {

        ChatRoomResponse chatRoom = chatServiceClient.createChatRoom(chatRoomCreateRequest);

        return chatRoom;
    }
}
