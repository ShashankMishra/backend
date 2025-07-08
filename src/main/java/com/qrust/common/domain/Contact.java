package com.qrust.common.domain;

import lombok.Data;

import static com.qrust.common.domain.Country.INDIA;

@Data
public class Contact {
    private String name;
    private Country country = INDIA;
    private String phoneNumber;
}
