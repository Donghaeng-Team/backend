package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipatingStaticsResponse {
    private Long activeAsCreator;
    private Long activeAsBuyer;
    private Long completed;
}
