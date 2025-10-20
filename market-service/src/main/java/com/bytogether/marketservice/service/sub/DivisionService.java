package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.DivisionServiceClient;
import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 행정구역 조회 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Service
@RequiredArgsConstructor
public class DivisionService {
    private final DivisionServiceClient divisionServiceClient;

    // 좌표 기반 행정구역 조회
    public DivisionResponseDto getDivisionByCoord(Double latitude, Double longitude) {
        return divisionServiceClient.getDivisionByCoord(latitude, longitude)
                .orElseThrow(() -> new RuntimeException("Invalid coordinates: no division found"));
    }

    // 행정구역 코드 기반 행정구역 조회
    public DivisionResponseDto getDivisionByCode(String emdCode) {
        return divisionServiceClient.getDivisionByCode(emdCode)
                .orElseThrow(() -> new RuntimeException("Invalid emdCode: no division found"));
    }


    // 행정구역 코드 기반 행정구역 조회
    public List<DivisionResponseDto> getNearDivisionsByCode(Integer depth, String emdCode) {
        List<DivisionResponseDto> nearDivisionsByCode = divisionServiceClient.getNearDivisionsByCode(depth, emdCode);
        if (nearDivisionsByCode.isEmpty()) {
            throw new RuntimeException("No nearby divisions found for the given code and depth");
        }
        return nearDivisionsByCode;
    }

}
