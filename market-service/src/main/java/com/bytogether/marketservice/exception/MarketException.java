package com.bytogether.marketservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * 마켓 관련 예외 처리 클래스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@Setter
public class MarketException extends RuntimeException {
    private final HttpStatus httpStatus;

    public MarketException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
