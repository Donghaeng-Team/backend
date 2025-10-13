package com.bytogether.marketservice.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * User Service에서 사용자 정보를 임시로 받아오기 위한 Mock DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String nickname;
    private String imageUrl;
}
