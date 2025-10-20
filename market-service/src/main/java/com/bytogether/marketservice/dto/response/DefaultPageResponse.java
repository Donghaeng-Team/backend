package com.bytogether.marketservice.dto.response;

import lombok.*;

/**
 * 페이지 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DefaultPageResponse {
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Boolean hasNext;
    private Boolean hasPrevious;
}
