package com.devtools.ai.exception;

import org.springframework.http.HttpStatus;

public class UsageLimitExceededException extends ApiException {
    public UsageLimitExceededException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "USAGE_LIMIT_EXCEEDED");
    }
}
