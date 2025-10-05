package com.bytogether.commservice.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static BusinessException of(HttpStatus status, String message) {
        return new BusinessException(status, message);
    }
}
