package com.qrust.exceptions;

public class LimitReachedException extends RuntimeException {
    public LimitReachedException(String s) {
        super(s);
    }
}
