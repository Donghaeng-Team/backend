package com.bytogether.divisionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Coordinate {
    // 제약 조건 추가 고려, 위도 서비스 범위 33~39, 경도 124~132
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
