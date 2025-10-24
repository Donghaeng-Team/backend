package com.bytogether.commservice.service;

import com.bytogether.commservice.Exception.BusinessException;
import com.bytogether.commservice.Exception.NeverOccuredException;
import com.bytogether.commservice.client.UserServiceClient;
import com.bytogether.commservice.client.dto.UserDto;
import com.bytogether.commservice.client.dto.UserInternalResponse;
import com.bytogether.commservice.client.dto.UsersInfoRequest;
import com.bytogether.commservice.dto.*;
import com.bytogether.commservice.dto.Enum.PostStatus;
import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.entity.PostLike;
import com.bytogether.commservice.entity.PostStat;
import com.bytogether.commservice.repository.PostLikeRepository;
import com.bytogether.commservice.repository.PostRepository;
import com.bytogether.commservice.repository.PostStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostStatRepository postStatRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final UserServiceClient userServiceClient;
    private final PostLikeRepository postLikeRepository;

    // 고정 페이징: 10개씩, 최신순
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    /**
     * com-01 :  게시글 목록 가져오기
     */
    public List<PostListResponse> getPostsList(String divisionCode,String tag,String keyword){
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        List<PostStat> postStats;


        if (keyword != null && !keyword.trim().isEmpty()) {
            postStats = postStatRepository.searchPosts(divisionCode, tag, keyword, pageable);
        } else {
            postStats = "all".equalsIgnoreCase(tag)
                    ? postStatRepository.findByRegion(divisionCode, pageable)
                    : postStatRepository.findByRegionAndTag(divisionCode, tag, pageable);
        }


        // userIds 추출
        List<Long> userIds = postStats.stream()
                .map(PostStat::getUserId)
                .distinct()
                .toList();

        // user-service에 일괄 요청 (배치 호출)
        List<UserInternalResponse> userResponse = userServiceClient.getUserInfo(new UsersInfoRequest(userIds));
        List<UserDto> userList = userResponse.stream().map(UserDto::from).toList();

        Map<Long, UserDto> userMap = userList.stream()
                .collect(Collectors.toMap(UserDto::getId, u -> u));

        return postStats.stream()
                .map(stat -> PostListResponse.from(stat, userMap.get(stat.getUserId())))
                .toList();
    }

    /**
     * com-02 : 게시글 상세 조회
     */
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + postId));

        PostStat stat = postStatRepository.findById(postId)
                .orElse(PostStat.builder()
                        .postId(postId)
                        .likeCount(0)
                        .commentCount(0)
                        .viewCount(0)
                        .build());

        UserDto userInfo = UserDto.from(userServiceClient.getUserInfo(new UsersInfoRequest(List.of(stat.getUserId()))).get(0));
        stat.setViewCount(stat.getViewCount()+1);
        postStatRepository.save(stat);
        return PostDetailResponse.from(post, stat,userInfo);
    }

    /**
     * com-03 : 특정 사용자가 작성한 게시글 목록 조회
     */
    public List<PostListResponse> getPostsByUser(Long userId) {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        List<Post> posts = postRepository.findByAuthorId(userId, pageable);

        return posts.stream()
                .map(PostListResponse::fromPostEntity)
                .toList();
    }



    //**  private **//
    /**
     * com-04 : 특정  유저가 게시글 작성
     */
    @Transactional
    public PostResponse createPostInit(Long userId, PostCreateAndUpdateRequest req){
        Post post = Post.builder()
                .authorId(userId)
                .region(req.getRegion())
                .tag(req.getTag())
                .title("(작성중)")
                .content("") // 비어 있음
                .status(PostStatus.TEMP) // 아직 미완성 상태
                .build();
        postRepository.saveAndFlush(post);
        return PostResponse.from(post);
    }


    /**
     * com-05 : 게시글 수정 (작성자 본인만)
     */
    @Transactional
    public PostResponse  updatePost(Long userId, Long postId, PostCreateAndUpdateRequest req) {
        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findByPostIdIncludeTemp(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 1.5 동작 수행 전 post의 상태 확인
        PostStatus firstStatus = post.getStatus();
        LocalDateTime now = LocalDateTime.now();

        // 2. 작성자 검증
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "본인만 생성/수정할 수 있습니다.");
        }

        // 3. 기존 이미지 목록 보관
        List<String> oldImages = post.getImageUrls() != null
                ? new ArrayList<>(post.getImageUrls())
                : new ArrayList<>();


        // 4. 필드 수정
        post.setRegion(req.getRegion());
        post.setTag(req.getTag());
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setImageUrls(
                req.getImages().stream()
                        .map(PostImageRegister::getS3Key)
                        .toList()
        );
        String thumbnailUrl = req.getImages().stream()
                .filter(img->img.getOrder()!=null && img.getOrder() == 0)
                .map(PostImageRegister::getS3Key)
                .map(s3Key -> s3Key.replaceFirst("1_", "0_"))
                .findFirst().orElse(null);
        post.setThumbnailUrl(thumbnailUrl);
        post.setStatus(PostStatus.PUBLISHED);

        if(firstStatus == PostStatus.PUBLISHED){
            post.setUpdatedAt(now);
        }else if(firstStatus == PostStatus.TEMP){
            post.setCreatedAt(now);
        }
        postRepository.save(post);


        //5. PostStat 동기화
        PostStat stat ;
        log.info("[PostStat Sync] postId={}, firstStatus={}", postId, firstStatus);

        switch (firstStatus) {
            case TEMP -> {
                // 새 게시글 최초 발행
                stat = PostStat.builder()
                        .post(post)
                        .userId(userId)
                        .region(req.getRegion())
                        .tag(req.getTag())
                        .title(req.getTitle())
                        .previewContent(extractPreview(req.getContent()))
                        .createdAt(now)
                        .thumbnailUrl(thumbnailUrl)
                        .status(PostStatus.PUBLISHED)
                        .build();
                log.info("[PostStat Create] 신규 생성 - postId={}, region={}, tag={}", postId, req.getRegion(), req.getTag());
            }

            case PUBLISHED -> {
                // 기존 게시글 업데이트
                stat = postStatRepository.findById(postId)
                        .orElseThrow(() -> {
                            log.error("[DataMismatch] PostStat 누락 - postId={}, status={}", postId, firstStatus);
                            return new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "PostStat 데이터가 존재하지 않습니다.");
                        });

                stat.setTitle(post.getTitle());
                stat.setTag(post.getTag());
                stat.setRegion(post.getRegion());
                stat.setPreviewContent(extractPreview(post.getContent()));
                stat.setThumbnailUrl(extractThumbnail(req));
                log.debug("[PostStat Update] postId={}, updated title={}", postId, stat.getTitle());
            }

            default -> throw new NeverOccuredException("로직적 불가능한 상태 - 게시물 최초 상태: " + firstStatus);
        }

        postStatRepository.save(stat);

        // 3️⃣ S3에서 이전 이미지 전부 삭제 (비동기)
        if (!oldImages.isEmpty()) {
            s3Service.deleteFilesAsync(oldImages);
        }

        return PostResponse.from(post);
    }

    /**
     * com-06 : 게시글 삭제 (작성자 본인만)
     */
    @Transactional
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
        stat.setStatus(PostStatus.DELETED);
        postStatRepository.save(stat);

    }

    /**
     * com-07 : 게시글 좋아요 증가
     */
    @Transactional
    public void increaseLikeCount(Long userId, Long postId) {
        // 게시글 확인 (삭제된 게시글 방지용)
        PostStat stat = postStatRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (stat.getStatus() != PostStatus.PUBLISHED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 게시글에는 좋아요를 누를 수 없습니다.");
        }

        // ✅ 중복 좋아요 방지
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다.");
        }

        postLikeRepository.save(
                PostLike.builder()
                        .postId(postId)
                        .userId(userId)
                        .createdAt(LocalDateTime.now())
                        .build()
        );


        stat.setLikeCount(stat.getLikeCount() + 1);
        postStatRepository.save(stat);
    }



    //이미지 업로드 시 사용되는 로직
    public boolean isOwnerOfPost(Long postId, Long userId) {
        return postRepository.findByPostIdIncludeTemp(postId)
                .map(post -> Objects.equals(post.getAuthorId(), userId))
                .orElse(false);
    }



    /// Common Methods
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





