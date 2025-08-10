package com.kubertest.java.demo.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoudException extends RuntimeException {
    public NotFoudException() {
        super();
    }

    public NotFoudException(String message) {
        super(message);
    }

    public NotFoudException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoudException(Throwable cause) {
        super(cause);
    }
  
}
