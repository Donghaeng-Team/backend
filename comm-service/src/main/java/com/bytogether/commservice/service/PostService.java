package com.bytogether.commservice.service;

import com.bytogether.commservice.Exception.BusinessException;
import com.bytogether.commservice.dto.*;
import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.entity.PostStat;
import com.bytogether.commservice.repository.PostRepository;
import com.bytogether.commservice.repository.PostStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostStatRepository postStatRepository;
    private final PostRepository postRepository;

    // 고정 페이징: 10개씩, 최신순
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    /**
     * com-01 :  게시글 목록 가져오기
     */
    public List<PostListResponse> getPostsList(String divisionCode,String tag){
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        List<PostStat> posts;

        if ("all".equalsIgnoreCase(tag)) {
            posts = postStatRepository.findByRegionAndDeletedFalse(divisionCode, pageable);
        } else {
            posts = postStatRepository.findByRegionAndTagAndDeletedFalse(divisionCode, tag, pageable);
        }

        return posts.stream()
                .map(PostListResponse::from)
                .toList();
    }

    /**
     * com-02 : 게시글 상세 조회
     */
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByPostIdAndDeletedIsFalse(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + postId));

        PostStat stat = postStatRepository.findById(postId)
                .orElse(PostStat.builder()
                        .postId(postId)
                        .likeCount(0)
                        .commentCount(0)
                        .viewCount(0)
                        .build());

        return PostDetailResponse.from(post, stat);
    }

    /**
     * com-03 : 특정 사용자가 작성한 게시글 목록 조회
     */
    public List<PostListResponse> getPostsByUser(Long userId) {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        List<Post> posts = postRepository.findByAuthorIdAndDeletedIsFalse(userId, pageable);

        return posts.stream()
                .map(PostListResponse::fromPostEntity)
                .toList();
    }




    //**  private **//
    /**
     * com-04 : 특정  유저가 게시글 작성
     */
    @Transactional
    public PostResponse createPost(Long userId, PostCreateAndUpdateRequest req){

        // 1. 필수 필드 검증
        validatePostRequest(req);

        // 2. 이미지 접근 검증
        validateImageAccess(userId, req.getImages());

        // 3. 썸네일 기록
        String thumbnailUrl = extractThumbnail(req);

        //4. Post 생성
        Post post = Post.builder()
                .region(req.getRegion())
                .tag(req.getTag())
                .authorId(userId)
                .title(req.getTitle())
                .content(req.getContent())
                .imageUrls(req.getImages() != null && !req.getImages().isEmpty()
                        ? req.getImages().stream()
                        .map(PostImageRegister::getS3Key)
                        .toList()
                        : null)
                .thumbnailUrl(thumbnailUrl)
                .build();
        postRepository.save(post);


        // 5. 통계(PostStat) 생성
        PostStat stat = PostStat.builder()
                .post(post)
                .region(req.getRegion())
                .tag(req.getTag())
                .title(req.getTitle())
                .previewContent(extractPreview(req.getContent()))
                .thumbnailUrl(thumbnailUrl)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        postStatRepository.save(stat);


        // 6. 반환.
        return PostResponse.from(post);

    }


    /**
     * com-05 : 게시글 수정 (작성자 본인만)
     */
    public PostResponse  updatePost(Long userId, Long postId, PostCreateAndUpdateRequest req) {
        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 2. 작성자 검증
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "본인만 수정할 수 있습니다.");
        }

        // 3. 필드 수정
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            post.setTitle(req.getTitle());
        }
        if (req.getContent() != null && !req.getContent().isBlank()) {
            post.setContent(req.getContent());
        }
        if (req.getTag() != null) {
            post.setTag(req.getTag());
        }
        if (req.getRegion() != null) {
            post.setRegion(req.getRegion());
        }
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            post.setImageUrls(
                    req.getImages().stream()
                            .map(PostImageRegister::getS3Key)
                            .toList()
            );
        }

        postRepository.save(post);

        // 4. PostStat 동기화
        PostStat stat = postStatRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "PostStat 데이터가 존재하지 않습니다."));

        stat.setTitle(post.getTitle());
        stat.setTag(post.getTag());
        stat.setRegion(post.getRegion());
        stat.setPreviewContent(extractPreview(post.getContent()));
        stat.setThumbnailUrl(extractThumbnail(req));
        postStatRepository.save(stat);

        return PostResponse.from(post);
    }

    /**
     * com-06 : 게시글 삭제 (작성자 본인만)
     */

    public void deletePost(Long userId, Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 2. 작성자 검증
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "본인만 삭제할 수 있습니다.");
        }

        // 3. 게시글 삭제
        postRepository.delete(post);

        // 4. PostStat 업데이트 (삭제 플래그 true)
        PostStat stat = postStatRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "PostStat 데이터가 존재하지 않습니다."));
        stat.setDeleted(true);
        postStatRepository.save(stat);

    }

    /**
     * com-07 : 게시글 좋아요 증가
     */
    public void increaseLikeCount(Long userId, Long postId) {
        // 게시글 확인 (삭제된 게시글 방지용)
        PostStat stat = postStatRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (stat.isDeleted()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "삭제된 게시글에는 좋아요를 누를 수 없습니다.");
        }

        stat.setLikeCount(stat.getLikeCount() + 1);
        postStatRepository.save(stat);

    }




    /// Common Methods


    private void validatePostRequest(PostCreateAndUpdateRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "제목은 필수입니다.");
        }
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "내용은 필수입니다.");
        }
    }
    private void validateImageAccess(Long userId, List<PostImageRegister> images) {
        if (images == null || images.isEmpty()) return;

        for (PostImageRegister img : images) {
            if (!img.getS3Key().startsWith("posts/" + userId + "/")) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "본인 영역 외 이미지 접근 금지");
            }
        }
    }
    private String extractPreview(String content) {
        if (content == null) return "";
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
    private String extractThumbnail(PostCreateAndUpdateRequest req) {
        if (req.getImages() == null || req.getImages().isEmpty()) return null;
        return req.getImages().stream()
                .filter(PostImageRegister::isThumbnail)
                .findFirst()
                .map(PostImageRegister::getS3Key)
                .orElse(req.getImages().get(0).getS3Key());
    }


}





