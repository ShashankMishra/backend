package com.qrust.user.service;

import com.qrust.user.exceptions.CustomUIException;

public class InvalidActionException extends CustomUIException {
    public InvalidActionException(String s) {
        super(s);
    }
}
