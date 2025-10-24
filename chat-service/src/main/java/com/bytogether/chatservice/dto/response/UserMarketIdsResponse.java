package com.bytogether.chatservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserMarketIdsResponse {
    private List<Long> ongoing;      // 모집진행중 ~ 구매진행중 (구매 참여 중)
    private List<Long> completed;    // 완료된 구매

    private Integer ongoingCount;
    private Integer completedCount;
}
