package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.entity.Image;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 공동 구매 마켓 이미지 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Service
@RequiredArgsConstructor
public class ImageService {
    private static final String IMAGE_DIR = "market/images/"; // 이미지 저장 디렉토리
    private static final String THUMBNAIL_DIR = "market/thumbnails/"; // 썸네일 이미지 접두사
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private final ImageHandler imageHandler;
    private final ImageRepository imageRepository;

    // 허용된 MIME 타입인지 확인
    public void isAllowedMimeType(List<MultipartFile> images) {
        // 이미지가 없으면 처리하지 않음
        if (images == null || images.isEmpty()) {
            return;
        }
        // 각 이미지의 MIME 타입 검사 및 파일 확장자 검사
        images.forEach(image -> {
            // MIME 타입 검사
            if (!ALLOWED_MIME_TYPES.contains(image.getContentType())) {
                throw new MarketException("Unsupported image type: " + image.getContentType(), HttpStatus.BAD_REQUEST);
            }
            // 파일 확장자 검사
            if (image.getOriginalFilename() == null || !image.getOriginalFilename().matches(".*\\.(jpg|jpeg|png|webp)$")) {
                throw new MarketException("Invalid file extension for image: " + image.getOriginalFilename(), HttpStatus.BAD_REQUEST);
            }

            // 이미지 확장자가 MIME 타입과 일치하는지 확인
            String fileExtension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            String mimeType = image.getContentType();
            if ((fileExtension.equals("jpg") || fileExtension.equals("jpeg")) && !mimeType.equals("image/jpeg") && !mimeType.equals("image/jpg")) {
                throw new MarketException("File extension does not match MIME type for image: " + image.getOriginalFilename(), HttpStatus.BAD_REQUEST);
            }
            if (fileExtension.equals("png") && !mimeType.equals("image/png")) {
                throw new MarketException("File extension does not match MIME type for image: " + image.getOriginalFilename(), HttpStatus.BAD_REQUEST);
            }
            if (fileExtension.equals("webp") && !mimeType.equals("image/webp")) {
                throw new MarketException("File extension does not match MIME type for image: " + image.getOriginalFilename(), HttpStatus.BAD_REQUEST);
            }
        });
    }




    public void handleImageWhenMarketCreate(List<MultipartFile> images, Long marketId) {
        if (images == null || images.isEmpty()) {
            return; // 이미지가 없으면 처리하지 않음
        }

        // 이미지 엔티티 저장을 위한 리스트
        List<Image> newImages = new ArrayList<>();

        // 비동기 업로드를 위한 CompletableFuture 리스트
        List<CompletableFuture<Void>> futures = new ArrayList<>();


        for (int i = 0; i < images.size(); i++) {

            MultipartFile image = images.get(i);
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            // 고유한 파일명 생성 ( marketId + order + UUID + 확장자 )
            String uuid = java.util.UUID.randomUUID().toString();
            String uniqueFilename = marketId + "_" + (i+1) + "_" + uuid + extension;
            System.out.println("uniqueFilename = " + uniqueFilename);
            String imagePath = IMAGE_DIR + uniqueFilename;

            // 이미지 업로드
            futures.add(CompletableFuture.runAsync(() -> imageHandler.uploadImageByS3(image, imagePath)));

            // 이미지 엔티티
            Image newImage = Image.createImage(marketId, i + 1, originalFilename, uniqueFilename, imagePath, image.getContentType());

            newImages.add(newImage);

            if (i == 0) {
                // 썸네일 이미지 추가 (0번 인덱스)
                String thumbnailUniqueFilename =  marketId + "_" + i + "_" + uuid + extension;
                Image newThumbnailImage = Image.createImage(marketId, 0, originalFilename, thumbnailUniqueFilename, THUMBNAIL_DIR + thumbnailUniqueFilename, image.getContentType());
                newImages.add(newThumbnailImage);
            }

        }
        imageRepository.saveAll(newImages);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }

    // 마켓 수정 시 이미지 처리
    public void handleImageWhenUpdate(List<MultipartFile> images, Long marketId) {
        List<Image> allByMarketId = imageRepository.findAllByMarketId(marketId);

        // 기존 이미지가 있을 경우
        if(!allByMarketId.isEmpty()){
            // S3에서 삭제
            allByMarketId.forEach(image -> imageHandler.deleteImageByS3(image.getFilePath()));

            // 기존 이미지 삭제 - 테이블에서 삭제
            imageRepository.deleteAll(allByMarketId);
        }

        // 새 이미지 업로드 및 저장
        handleImageWhenMarketCreate(images, marketId);
    }
}
