package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.UserServiceClient;
import com.bytogether.marketservice.client.dto.response.MockUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User Service와 통신하는 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    public MockUserDto getUserById(Long userId) {
        return userServiceClient.getUserById(userId);
    }

    public List<MockUserDto> getUsersByIds(List<Long> authorIds) {
        return userServiceClient.getUsersByIds(authorIds);
    }

}
