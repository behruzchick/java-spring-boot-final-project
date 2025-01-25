package com.example.demo.security;
import org.springframework.security.core.AuthenticationException;
public class UserNotActivateException extends AuthenticationException {
    public UserNotActivateException(String explanation) {
        super(explanation);
    }

    public UserNotActivateException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
