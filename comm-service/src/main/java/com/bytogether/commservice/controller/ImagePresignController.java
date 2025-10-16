package com.bytogether.commservice.controller;

import com.bytogether.commservice.Exception.BusinessException;
import com.bytogether.commservice.dto.UploadUrlsRequest;
import com.bytogether.commservice.dto.UploadUrlsResponse;
import com.bytogether.commservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ImagePresignController {

    private final S3Service s3Service;

    @PostMapping("/upload-url/{postId}")
    public UploadUrlsResponse generateUploadUrls(@RequestHeader("X-User-Id") Long userId,
                                                 @PathVariable Long postId,
                                                 @RequestBody UploadUrlsRequest request) {
        if (request.getFiles().size() > 5) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "이미지는 최대 5개까지 업로드할 수 있습니다.");
        }

        List<UploadUrlsResponse.UploadUrl> urls = request.getFiles().stream()
                .map(file -> {
                    String s3Key = String.format(
                            "comm-service/posts/%d/%d_%s_%s",
                            postId,
                            file.getIndex() + 1, // 사진 순서는 자연수로 적용
                            UUID.randomUUID(),
                            file.getFileName()
                    );
                    String presignedUrl = s3Service.generatePresignedUrl(s3Key, file.getContentType());
                    return new UploadUrlsResponse.UploadUrl(presignedUrl, s3Key);
                })
                .toList();

        return new UploadUrlsResponse(urls);
    }
}
