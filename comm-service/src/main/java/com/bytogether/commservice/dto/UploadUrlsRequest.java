package com.bytogether.commservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UploadUrlsRequest {
    private List<FileInfo> files;

    @Getter @Setter
    public static class FileInfo {
        private Integer index;       // 업로드 순서
        private String fileName;
        private String contentType;
    }
}