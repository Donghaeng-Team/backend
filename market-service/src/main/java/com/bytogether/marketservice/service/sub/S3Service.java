package com.bytogether.marketservice.service.sub;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

/**
 * S3 업로더 서비스 클래스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
public class S3Service {
    private final S3Client s3Client;

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전 등
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Async
    public void uploadFile(String bucketName, String key, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    @Async
    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key).build());
    }

}
