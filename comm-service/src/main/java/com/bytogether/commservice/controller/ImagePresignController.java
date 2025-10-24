package com.bytogether.commservice.controller;

import com.bytogether.commservice.Exception.BusinessException;
import com.bytogether.commservice.dto.UploadUrlsRequest;
import com.bytogether.commservice.dto.UploadUrlsResponse;
import com.bytogether.commservice.service.PostService;
import com.bytogether.commservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/private")
@RequiredArgsConstructor
public class ImagePresignController {

    private final S3Service s3Service;
    private final PostService postService;


    @PostMapping("/upload-url/{postId}")
    public UploadUrlsResponse generateUploadUrls(@RequestHeader("X-User-Id") Long userId,
                                                 @PathVariable Long postId,
                                                 @RequestBody UploadUrlsRequest request) {

        // ✅ 1. postId와 userId 일치 여부 검증
        if (!postService.isOwnerOfPost(postId, userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "해당 게시물의 소유자가 아닙니다.");
        }

        // ✅ 2. 이미지 개수 검증
        if (request.getFiles().size() > 5) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "이미지는 최대 5개까지 업로드할 수 있습니다.");
        }

        // ✅ 3. Presigned URL 생성
        List<UploadUrlsResponse.UploadUrl> urls = request.getFiles().stream()
                .map(file -> {
                    String s3Key = String.format(
                            "static/posts/images/%d/%d_%s_%s",
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
