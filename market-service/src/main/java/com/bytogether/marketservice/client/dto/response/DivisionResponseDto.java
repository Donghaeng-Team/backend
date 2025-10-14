package com.bytogether.marketservice.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Division Service에서 행정구역 정보를 받아오기 위한 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DivisionResponseDto {
    //    {
//        "id": "string",
//            "sidoCode": "string",
//            "sidoName": "string",
//            "sggCode": "string",
//            "sggName": "string",
//            "emdCode": "string",
//            "emdName": "string",
//            "centroidLat": 0,
//            "centroidLng": 0
//    }
    private String id;
    private String sidoCode;
    private String sidoName;
    private String sggCode;
    private String sggName;
    private String emdCode;
    private String emdName;
    private Double centroidLat;
    private Double centroidLng;
}
