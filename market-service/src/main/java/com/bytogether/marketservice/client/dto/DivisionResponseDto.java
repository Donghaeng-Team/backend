package com.bytogether.marketservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
