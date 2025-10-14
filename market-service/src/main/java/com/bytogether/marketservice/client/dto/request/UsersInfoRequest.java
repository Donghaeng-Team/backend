package com.bytogether.marketservice.client.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User Service에 사용자 정보를 요청하기 위한 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-14
 */

@Getter
@Setter
public class UsersInfoRequest {
    private List<Long> userIds;
}
