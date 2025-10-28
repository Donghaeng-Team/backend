package com.bytogether.chatservice.exception;

import com.bytogether.chatservice.dto.common.ApiResponse;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatForbiddenException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleChatForbiddenException(ChatForbiddenException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse();

        error.setTimestamp(LocalDateTime.now());
        error.setStatus(ex.getHttpStatus().value());
        error.setError(ex.getHttpStatus().getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        log.error("ChatForbiddenException - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail(error.getMessage());


        return new ResponseEntity<>(apiResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotFoundException(
            Exception ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        log.error("NotFoundException - error: {}", error);

        ApiResponse<ErrorResponse> apiResponse = ApiResponse.fail("서버 오류가 발생했습니다.");

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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
