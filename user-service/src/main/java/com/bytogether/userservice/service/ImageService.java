package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.response.ImageUploadResponse;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final UserRepository userRepository;
    private final UserService userService;

    private static final List<String> ALLOWED_EXTENSIONS =
            List.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketname;

    public ImageUploadResponse uploadImage(Long userId, MultipartFile file) {
        try {
            //1. 전달된 파일 유효성 검증
            if(file == null || file.isEmpty()){
                throw new IllegalArgumentException("File is null or empty");
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new IllegalArgumentException("유효한 파일명이 아닙니다.");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            validateImageFile(file, extension);

            // 2. 기존 파일이 있는 경우 삭제(파일 교체의 경우)
            User user = userService.getOptionalUserById(userId).orElseThrow(
                    () -> new IllegalStateException("사용자가 존재하지 않습니다.")
            );

            if(user.getAvatar() != null && !user.getAvatar().isBlank()) {
                deleteOldAvatar(user.getId(), user.getAvatar());
            }

            //3. S3에 저장
            String filename = String.format(
                    "static/user/image/%d/%s_%s",
                    userId, UUID.randomUUID(), file.getOriginalFilename()
            );
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketname)
                    .key(filename)
                    .contentType(file.getContentType())
                    .contentDisposition("inline")
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize());

            s3Client.putObject(
                    putObjectRequest,
                    requestBody
            );

            log.info("S3 업로드 성공 - 버킷: {}, 키: {}", bucketname, filename);

            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketname, "ap-northeast-2", filename);

            //4. 이미지 URL 저장 후 반환
            user.setAvatar(imageUrl);
            userRepository.save(user);
            log.info("이미지 Url 저장: {}", imageUrl);
            return ImageUploadResponse.builder()
                    .imageUrl(imageUrl)
                    .build();
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void validateImageFile(MultipartFile file, String extension){
        if(file.getSize() > 5 * 1024 * 1024 ){
            throw new IllegalArgumentException("이미지의 크기는 5MB를 초과할 수 없습니다.");
        }
        if(!ALLOWED_EXTENSIONS.contains(extension)){
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.") ;
        }
    }

    private void deleteOldAvatar(Long userId, String oldAvatar){
        try{
            URI uri = new URI(oldAvatar);
            String oldAvatarPath = uri.getPath();
            if( oldAvatarPath.length() <= 1){
                log.warn("경로가 유효하지 않습니다");
                return;
            }
            String s3Key = oldAvatarPath.substring(1);
            log.info("Key 추출: {}", s3Key);
            s3Client.deleteObject(builder ->
                    builder.bucket(bucketname).key(s3Key));
        }catch(Exception e){
            log.error("삭제 실패: {}", e.getMessage());
        }
    }
}
