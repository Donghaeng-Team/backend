package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.response.MockUserDto;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * User Service와 통신하기 위한 Feign Client
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-31
 */

// TODO: 실제 User Service가 준비되면 구현체로 변경 - 2025-10-10
@FeignClient(name = "user-service")
public interface UserServiceClient {

    default MockUserDto getUserById(Long userId) {
        return new MockUserDto(userId, "mockUser", "mockImageUrl");
    }

    default List<MockUserDto> getUsersByIds(List<Long> authorIds) {
        return authorIds.stream().map(id -> new MockUserDto(id, "mockUser" + id, "mockImageUrl" + id)).toList();
    }

}
