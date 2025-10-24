package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.request.ResetPasswordRequest;
import com.bytogether.userservice.dto.request.VerifyRequest;
import com.bytogether.userservice.dto.verify.VerifyData;
import com.bytogether.userservice.model.InitialProvider;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.model.VerifyType;
import com.bytogether.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserVerifyService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LambdaService lambdaService;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    private static final String EMAIL_VERIFY_PREFIX = "email_verify:";
    private static final String PASSWORD_VERIFY_PREFIX = "password_reset:";
    private static final Long EMAIL_VERIFY_EXPIRE_HOUR = 24L;

    // 인증 정보 저장
    public void save(String email, String token, VerifyType verifyType) {
        //1.redis에 저장할 정보 설정 (Provider중 Local 가입의 사용자의 메일만 비교검증, 전체메일 중복가능, Local만 중복불가)
       String key = getKey(token, verifyType);
       Map<String, String> data = new HashMap<>();
       data.put("email", email);
       data.put("verifyType", verifyType.name());
       data.put("provider", InitialProvider.LOCAL.name());

       //2. 정보 저장
       redisTemplate.opsForHash().putAll(key, data);
       redisTemplate.expire(key, EMAIL_VERIFY_EXPIRE_HOUR, TimeUnit.HOURS);
       log.info("Redis에 저장 완료: key:{}, email:{} verifyType:{}", key, email, verifyType);
    }

    // EMail 인증
    @Transactional
    public void verifyEmail(VerifyRequest verifyRequest) {
        String token = verifyRequest.getToken();
        VerifyType type = verifyRequest.getType();

        VerifyData data = validateToken(token, type);
        User user = getUser(data);
        user.setVerify(true);
        userRepository.save(user);
        delete(token,type);
        log.info("이메일 인증 완료");

//        //front에서 bucket설정정보 삽입으로 경로만 db저장 로직 변경
//        String defaultAvatarUrl = String.format(
//                "https://%s.s3.ap-northeast-2.amazonaws.com/static/user/default/avatar-default.png",
//                bucketName
//        );

        //default image설정
        user.setAvatar("/static/user/default/avatar-default.png");
        userRepository.save(user);
        try {
            String avatarUrl = getAvatarFromAI(user.getId(), user.getNickname());
            URI uri = new URI(avatarUrl);
            String newAvatarPath = uri.getPath();
            if( newAvatarPath.length() <= 1){
                log.warn("경로가 유효하지 않습니다");
                return;
            }
            String pathInfo = newAvatarPath;
            user.setAvatar(pathInfo);
            log.info("아바타 정보 추출완료");
            userRepository.save(user);
        }catch(Exception e) {
            log.error("AI 아바타 생성 실패, 기본 이미지 사용 - userId: {}", user.getId(), e);// ← 여기서 기본 이미지
        }
    }

    //Password 재발급
    @Transactional
    public void resetPassword( ResetPasswordRequest request) {
        String token = request.getToken();
        String password = request.getPassword();
        VerifyType type = request.getType();

        VerifyData data = validateToken(request.getToken(), VerifyType.PASSWORD);
        User user = getUser(data);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        delete(token, type);
        log.info("비밀번호 변경 완료");
    }

    public void delete(String token, VerifyType verifyType) {
        String key = getKey(token, verifyType);
        redisTemplate.delete(key);
        log.info("Redis key 삭제: {} ", key);
    }

    //키 설정
    private String getKey(String token, VerifyType verifyType) {
        if (verifyType == VerifyType.EMAIL) {
            return EMAIL_VERIFY_PREFIX + token;
        } else if (verifyType == VerifyType.PASSWORD) {
            return PASSWORD_VERIFY_PREFIX + token;
        } else {
            throw new RuntimeException("verifyType이 유효하지 않습니다");
        }
    }

    //redis내에 저장된 인증 정보 확인
    private VerifyData validateToken(String token, VerifyType verifyType){
        String key = getKey( token, verifyType);
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

        if(data.isEmpty()) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

        String email = (String) data.get("email");
        InitialProvider provider = InitialProvider.valueOf(data.get("provider").toString());
        VerifyType type = VerifyType.valueOf(data.get("verifyType").toString());;

        log.info("검색된 데이터_ 이메일: {}, verifyType :{} ", email, verifyType);
        if(verifyType != type){
            throw new RuntimeException("적절한 인증토큰이 아닙니다");
        }
        return new VerifyData(email, verifyType, provider);

    }

    //인증 정보를 이용해 사용자 정보 얻기
    private User getUser(VerifyData verifyData) {
        User user = userRepository.findByEmailAndProviderAndDeletedAtIsNull(verifyData.getEmail(), verifyData.getInitialProvider())
                .orElseThrow(() -> new IllegalStateException("사용자가 없습니다.")
                );
        VerifyType verifyType = verifyData.getVerifyType();
        if(verifyType == VerifyType.EMAIL && user.getVerify()){
            throw new IllegalStateException("이미 인증되었습니다");
        }
        return user;
    }

    //사용자의 Avatar생성하여 avatarUrl반환
    private String getAvatarFromAI(Long userId, String nickname) {
        try {
            String avatarUrl = String.format(
                    "https://%s.s3.ap-northeast-2.amazonaws.com/static/user/thumbnails/%d/defaultImage.jpeg",
                    bucketName,
                    userId
            );
            String s3Key = String.format(
                    "static/user/thumbnails/%d/defaultImage.jpeg",
                    userId
            );
            lambdaService.invokeCreateAvatarFunction(userId,nickname, s3Key);
            log.info("AI 아바타 생성요청 : userId: {}, S3 Key: {}, nickname: {}", userId, s3Key, nickname);
            return avatarUrl;
        } catch (Exception e) {
            log.error("Lambda 호출실패");
            throw new RuntimeException(e);
        }
    }
}

