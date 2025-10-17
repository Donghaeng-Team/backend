package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.response.ApiResponse;
import com.bytogether.userservice.dto.response.ImageUploadResponse;
import com.bytogether.userservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PutMapping("private/me/image")
        public ResponseEntity<ApiResponse<?>> uploadImage(
                @RequestHeader("X-User-Id") Long userId,
                @RequestPart("image") MultipartFile file
                ){
       ImageUploadResponse response = imageService.uploadImage(userId, file);
       return ResponseEntity.ok(ApiResponse.success(response));
    }
}
