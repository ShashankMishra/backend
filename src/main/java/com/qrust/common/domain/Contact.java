package com.qrust.common.domain;

import io.quarkus.arc.All;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;

import static com.qrust.common.domain.Country.INDIA;

@Data
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private String name;
    @Builder.Default
    private Country country = INDIA;
    private String phoneNumber;
    @Builder.Default
    private ContactPreference preference = new ContactPreference();

    public boolean isAvailableNow() {
        return  preference.getContactPolicy().isAvailableNow(Instant.now());
    }
}
