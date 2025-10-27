package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.request.AvatarCallbackRequest;
import com.bytogether.userservice.dto.response.ApiResponse;
import com.bytogether.userservice.dto.response.ImageUploadResponse;
import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.repository.UserRepository;
import com.bytogether.userservice.service.ImageService;
import com.bytogether.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${spring.cloud.aws.lambda.api-key}")
    private String lambdaApiKey;

    //사용자 프로필 이미지 변경
    @PutMapping("private/me/image")
        public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
                @RequestHeader("X-User-Id") Long userId,
                @RequestPart("image") MultipartFile file
                ){
       ImageUploadResponse response = imageService.uploadImage(userId, file);
       return ResponseEntity.ok(ApiResponse.success(response));
    }

    //기본 이미지 callback
    @PutMapping("/public/{userId}/avatar/callback")
    public ResponseEntity<Void> avatarCallback(
            @PathVariable Long userId,
            @RequestBody AvatarCallbackRequest request,
            @RequestHeader("X-API-Key") String apiKey
    ){
        imageService.callback(userId, request, apiKey);
        return ResponseEntity.ok().build();
    }
}
