package com.qrust.common.domain;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import static com.qrust.common.domain.Country.INDIA;

@Data
@DynamoDbBean
public class Contact {
    private String name;
    private Country country = INDIA;
    private String phoneNumber;
    private ContactPreference preference = new ContactPreference();
}
