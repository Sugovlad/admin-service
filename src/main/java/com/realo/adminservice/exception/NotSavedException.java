package com.realo.adminservice.exception;

public class NotSavedException extends RuntimeException {
    public NotSavedException(String message) {
        super(message);
    }

    public NotSavedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}