package com.bytogether.marketservice.exception;

import com.bytogether.marketservice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * 전역 예외 처리 클래스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MarketException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMarketException(MarketException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();

        error.setTimestamp(LocalDateTime.now());
        error.setStatus(ex.getHttpStatus().value());
        error.setError(ex.getHttpStatus().getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        log.error("MarketException - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(error.getMessage());


        return new ResponseEntity<>(apiResponse, ex.getHttpStatus());
    }

    // Handle MethodArgumentNotValidException (thrown when @Valid fails)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {


        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors.toString(),
                request.getDescription(false).replace("uri=", "")
        );

        log.error("MethodArgumentNotValidException - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(error.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        log.error("IllegalArgumentException - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail("잘못된 요청입니다.");

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false)
        );

        log.error("Exception - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail("서버 오류가 발생했습니다.");

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
