package com.qrust.common.domain;

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

    public static BloodGroup fromString(String value) {
        for (BloodGroup bg : BloodGroup.values()) {
            if (bg.bloodGroupValue.equalsIgnoreCase(value)) {
                return bg;
            }
        }
        throw new IllegalArgumentException("Unknown blood group: " + value);
    }

    @Override
    public String toString() {
        return bloodGroupValue;
    }
}
