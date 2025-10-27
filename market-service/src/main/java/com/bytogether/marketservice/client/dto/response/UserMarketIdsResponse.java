package com.bytogether.marketservice.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserMarketIdsResponse {
    private List<Long> ongoing;      // 모집진행중 ~ 구매진행중 (구매 참여 중)
    private List<Long> completed;    // 완료된 구매

    private Integer ongoingCount;
    private Integer completedCount;
}
