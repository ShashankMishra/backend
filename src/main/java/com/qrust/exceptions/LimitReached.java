package com.qrust.exceptions;

public class LimitReached extends RuntimeException {
    public LimitReached(String s) {
        super(s);
    }
}
