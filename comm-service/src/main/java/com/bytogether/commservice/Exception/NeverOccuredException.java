package com.bytogether.commservice.Exception;

public class NeverOccuredException extends RuntimeException {
    public NeverOccuredException(String message) {
        super(message);
    }
    public NeverOccuredException() {
        super("논리적으로 발생할 수 없는 예외 상태입니다.");
    }
}