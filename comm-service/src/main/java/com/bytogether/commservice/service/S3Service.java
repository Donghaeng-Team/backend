package com.bytogether.commservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
public class S3Service {

    private final S3Presigner s3Presigner; // SDK v2 기반 Presigner
    private final String bucketName;

    public S3Service(
            S3Presigner s3Presigner,
            @Value("${s3.bucket.commImg}") String bucketName) {
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    public String generatePresignedUrl(String fileName, String contentType) {
        // S3 객체 업로드 요청 정의
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        // Presigned URL 생성 요청 정의
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // URL 유효시간 5분
                .putObjectRequest(objectRequest)
                .build();

        // Presigned URL 생성
        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        // 생성된 URL 반환
        return presigned.url().toString();
    }
}
