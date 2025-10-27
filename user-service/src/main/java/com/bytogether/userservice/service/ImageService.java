package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.request.AvatarCallbackRequest;
import com.bytogether.userservice.dto.response.ImageUploadResponse;
import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

//import java.net.URI;
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
    private String bucketName;

    @Value("${spring.cloud.aws.lambda.api-key}")
    private String lambdaApiKey;

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
                deleteOldAvatar(user.getAvatar());
            }

            //3. Key와 Thumbnail Key 생성
            String originalKey = String.format(
                    "static/user/images/%d/%s_%s",
                    userId, UUID.randomUUID(), originalFilename
            );
            int dotIndex = originalKey.lastIndexOf(".");
            String thumbnailKey = (originalKey.substring(0, dotIndex)+".jpg")
                    .replace("/images/", "/thumbnails/");


            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(originalKey)
                    .contentType(file.getContentType())
                    .contentDisposition("inline")
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize());

            s3Client.putObject(
                    putObjectRequest,
                    requestBody
            );

            log.info("S3 업로드 성공 - 버킷: {}, 키: {}", bucketName, originalKey);
            log.info("S3 thumbnail - 버킷: {}, 키: {}", bucketName, thumbnailKey);

            //Front Bucketname 변경으로 경로 조정하여 key사용
//            String imageUrl = String.format(
//                    "https://%s.s3.%s.amazonaws.com/%s",
//                    bucketName, "ap-northeast-2", originalKey);
//            String thumbnailUrl = String.format(
//                    "https://%s.s3.%s.amazonaws.com/%s",
//                    bucketName, "ap-northeast-2", thumbnailKey);

            //4. 이미지 URL 저장 후 반환

            //front 절대 경로처리
            String thumbnailKeySave = "/"+thumbnailKey;
            user.setAvatar(thumbnailKeySave);
            userRepository.save(user);
            log.info("이미지 Url 저장: {}", thumbnailKey);

            return ImageUploadResponse.builder()
                    .imageUrl(originalKey)
                    .thumbnailUrlSave(thumbnailKeySave)
                    .build();

        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void callback(Long userId, AvatarCallbackRequest request,String apiKey ){
        if(!lambdaApiKey.equals(apiKey)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Token");
        }
        //User가 없는 경우에 대한 예외처리 ??
        User user = userService.getOptionalUserById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if( user == null || !user.getVerify() || !(user.getProvider() == InitialProvider.LOCAL)){
            log.warn("이메일 가입 사용자가 아니거나 이메일 인증이 완료되지 않았습니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized User");
        }

        if(request.isSuccess ()){
            log.info("생성성공");
            user.setAvatar(request.getAvatarUrl());
            userRepository.save(user);
        }else {
            log.error("생성실패");
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

    // 아바타 필드 삭제
    private void deleteOldAvatar(String oldAvatar){
        try{
            //avatar 필드에 버킷정보가 제외되어 불필요한 부분 주석
//            URI uri = new URI(oldAvatar);
//            String oldAvatarPath = uri.getPath();
//            if( oldAvatarPath.length() <= 1){
//                log.warn("경로가 유효하지 않습니다");
//                return;
//            }
//            String s3Key = oldAvatarPath.substring(1);

            if(oldAvatar == null || oldAvatar.length() <= 1){
                log.warn("경로가 유효하지 않습니다");
                return;
            }

            String s3Key = oldAvatar.startsWith("/")
                    ? oldAvatar.substring(1)
                    : oldAvatar;

            log.info("Key 추출: {}", s3Key);
            s3Client.deleteObject(builder ->
                    builder.bucket(bucketName).key(s3Key));
        }catch(Exception e){
            log.error("삭제 실패: {} : {}", oldAvatar, e.getMessage());
        }
    }

}
