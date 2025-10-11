package com.bytogether.commservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UploadUrlsResponse {
    private List<UploadUrl> urls;

    @Getter
    @AllArgsConstructor
    public static class UploadUrl {
        private String uploadUrl;
        private String s3Key;
    }
}