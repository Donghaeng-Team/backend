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
    private static final String IMAGE_DIR = "images/market/"; // 이미지 저장 디렉토리
    private static final String THUMBNAIL_DIR = IMAGE_DIR + "thumbnail/"; // 썸네일 이미지 접두사
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/jpg", "image/png", "image/webp");
    private final ImageUploader imageUploader;
    private final ImageRepository imageRepository;

    public void isAllowedMimeType(List<MultipartFile> images) {
        images.forEach(image -> {
            if (!ALLOWED_MIME_TYPES.contains(image.getContentType())) {
                throw new MarketException("Unsupported image type: " + image.getContentType(), HttpStatus.BAD_REQUEST);
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
            String uniqueFilename = marketId + "_" + (i+1) + "_" + java.util.UUID.randomUUID() + extension;
            System.out.println("uniqueFilename = " + uniqueFilename);
            String imagePath = IMAGE_DIR + uniqueFilename;

            // 이미지 업로드
//            imageUploader.uploadImageByS3(image, imagePath);
            futures.add(CompletableFuture.runAsync(() -> imageUploader.uploadImageByS3(image, imagePath)));

            // 이미지 엔티티
            Image newImage = Image.createImage(marketId, i + 1, originalFilename, uniqueFilename, imagePath, image.getContentType());

            newImages.add(newImage);

            if (i == 0) {
                // 썸네일 이미지 추가 (0번 인덱스)
                String thumbnailUniqueFilename =  marketId + "_" + i + "_" + java.util.UUID.randomUUID() + extension;
                Image newThumbnailImage = Image.createImage(marketId, 0, originalFilename, thumbnailUniqueFilename, THUMBNAIL_DIR + thumbnailUniqueFilename, image.getContentType());
                newImages.add(newThumbnailImage);
            }

        }
        imageRepository.saveAll(newImages);

        // 모든 업로드 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }
}
