package com.bytogether.marketservice.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * User Service에서 사용자 정보를 받아오기 위한 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInternalResponse {
    private Long userId;
    private String nickName;
    private String imageUrl;
}
