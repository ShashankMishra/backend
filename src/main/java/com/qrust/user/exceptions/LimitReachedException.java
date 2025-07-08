package com.qrust.user.exceptions;

public class LimitReachedException extends RuntimeException {
    public LimitReachedException(String s) {
        super(s);
    }
}
