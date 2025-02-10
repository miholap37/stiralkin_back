package com.mailsender.mailSender.utils;


public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String rateLimitExceeded) {
        super(rateLimitExceeded);
    }
}
