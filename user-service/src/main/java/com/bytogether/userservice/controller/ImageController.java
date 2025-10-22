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

    @PutMapping("private/me/image")
        public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
                @RequestHeader("X-User-Id") Long userId,
                @RequestPart("image") MultipartFile file
                ){
       ImageUploadResponse response = imageService.uploadImage(userId, file);
       return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/public/{userId}/avatar/callback")
    public ResponseEntity<Void> avatarCallback(
            @PathVariable Long userId,
            @RequestBody AvatarCallbackRequest request,
            @RequestHeader("X-API-Key") String apiKey
    ){
        if(!lambdaApiKey.equals(apiKey)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //User가 없는 경우에 대한 예외처리 ??
        User user = userService.getOptionalUserById(userId).orElse(null);
        if( user == null || !user.getVerify() || !(user.getProvider() == InitialProvider.LOCAL)){
            log.warn("이메일 가입 사용자가 아니거나 이메일 인증이 완료되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

       if(request.isSuccess ()){
           log.info("생성성공");
           user.setAvatar(request.getAvatarUrl());
           userRepository.save(user);
       }else {
           log.error("생성실패");
           //생성 실패시 대체 이미지
       }
        return ResponseEntity.ok().build();
    }
}
