package com.qrust.domain;

public enum BloodGroup {
    // write all blood groups
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    DONT_KNOW("Don't Know");

    private final String bloodGroupValue;

    BloodGroup(String value) {
        this.bloodGroupValue = value;
    }


}
