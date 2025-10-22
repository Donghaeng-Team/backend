package com.bytogether.chatservice.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageSendRequest {
    private String messageContent;
}
