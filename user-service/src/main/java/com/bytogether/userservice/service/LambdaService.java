package com.bytogether.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaService {

    private final LambdaClient avatarLambdaClient;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.lambda.avatar-function}")
    private String avatarLambdaFunction;

    public void invokeCreateAvatarFunction(Long userId, String nickname, String s3Key){
        try{
          String payload = String.format(
                  "{\"userId\":%d,\"nickname\":\"%s\",\"s3Key\":\"%s\"}", userId, nickname, s3Key
          );
          InvokeRequest invokeRequest = InvokeRequest.builder()
                  .functionName(avatarLambdaFunction)
                  .invocationType(InvocationType.EVENT)
                  .payload(SdkBytes.fromUtf8String(payload))
                  .build();

          avatarLambdaClient.invoke(invokeRequest);
          log.info("Lambda 실행성공 - userId : {} ", userId);

        }catch (Exception e){
            log.error("Lamba 실행성공 - userId : {} ", userId);
        }
    }
}
