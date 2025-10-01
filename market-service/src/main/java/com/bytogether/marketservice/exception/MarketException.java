package com.bytogether.marketservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class MarketException extends RuntimeException {
    private final HttpStatus httpStatus;

    public MarketException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
