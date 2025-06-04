package com.qrust.domain;

public enum Country {

    INDIA ("+91");

    private String countryCode;

    Country(String countryCode) {
        this.countryCode = countryCode;
    }
}
