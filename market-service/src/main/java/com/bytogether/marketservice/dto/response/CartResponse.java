package com.bytogether.marketservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카트 응답 DTO
 *
 * @author insu9058
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
}
