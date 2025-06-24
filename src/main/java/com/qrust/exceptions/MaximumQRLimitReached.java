package com.qrust.exceptions;

public class MaximumQRLimitReached extends RuntimeException {
    public MaximumQRLimitReached(String s) {
        super(s);
    }
}
