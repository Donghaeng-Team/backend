package com.bytogether.marketservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ErrorResponse {

    @JsonIgnore
    private LocalDateTime timestamp;

    private int status;

    @JsonIgnore
    private String error;

    private String message;

    @JsonIgnore
    private String path;
}
