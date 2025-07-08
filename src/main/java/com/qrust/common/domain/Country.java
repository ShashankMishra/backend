package com.qrust.common.domain;

public enum Country {

    INDIA ("+91");

    private String countryCode;

    Country(String countryCode) {
        this.countryCode = countryCode;
    }
}
