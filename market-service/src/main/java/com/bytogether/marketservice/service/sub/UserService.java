package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.UserServiceClient;
import com.bytogether.marketservice.client.dto.request.UsersInfoRequest;
import com.bytogether.marketservice.client.dto.response.UserInternalResponse;
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

    public UserInternalResponse getUserById(Long userId) {
        UsersInfoRequest usersInfoRequest = new UsersInfoRequest();
        usersInfoRequest.setUserIds(List.of(userId));
        List<UserInternalResponse> usersByIds = userServiceClient.getUsersByIds(usersInfoRequest);
        if (usersByIds.isEmpty()) {
            throw new MarketException("User not found for id: " + userId, HttpStatus.NOT_FOUND);
        }
        return usersByIds.stream().findFirst().orElse(null);
    }

    public List<UserInternalResponse> getUsersByIds(List<Long> authorIds) {
        UsersInfoRequest usersInfoRequest = new UsersInfoRequest();
        usersInfoRequest.setUserIds(authorIds);
        List<UserInternalResponse> usersByIds = userServiceClient.getUsersByIds(usersInfoRequest);
        if (usersByIds.isEmpty()) {
            throw new MarketException("Users not found for ids: " + authorIds, HttpStatus.NOT_FOUND);
        }
        return usersByIds;
    }

}
