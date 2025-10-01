package com.bytogether.marketservice.service.sub;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageHandler {
    private static final String BUCKET_NAME = "kis-test-dev";

    private final S3Service s3Service;

    @Async
    public void uploadImageByS3(MultipartFile image, String key) {
        try {
            s3Service.uploadFile(BUCKET_NAME, key, image.getInputStream(), image.getSize(), image.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }


    @Async
    public void deleteImageByS3(String filePath) {
        s3Service.deleteFile(BUCKET_NAME, filePath);
    }
}
