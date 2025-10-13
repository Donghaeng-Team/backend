package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.UserServiceClient;
import com.bytogether.marketservice.client.dto.response.UserDto;
import com.bytogether.marketservice.exception.MarketException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public UserDto getUserById(Long userId) {
        List<UserDto> usersByIds = userServiceClient.getUsersByIds(List.of(userId));
        if (usersByIds.isEmpty()) {
            throw new MarketException("User not found for id: "+ userId, HttpStatus.NOT_FOUND);
        }
        return userServiceClient.getUsersByIds(List.of(userId)).stream().findFirst().orElse(null);
    }

    public List<UserDto> getUsersByIds(List<Long> authorIds) {
        List<UserDto> usersByIds = userServiceClient.getUsersByIds(authorIds);
        if (usersByIds.isEmpty()) {
            throw new MarketException("Users not found for ids: "+ authorIds, HttpStatus.NOT_FOUND);
        }
        return usersByIds;
    }

}
