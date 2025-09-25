package com.qrust.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrust.user.api.dto.ContactPolicyDto;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

import java.beans.Transient;

@Data
@DynamoDbBean
public class ContactPreference {
    private ContactPolicyDto contactPolicy = ContactPolicyDto.ALWAYS;


    @DynamoDbIgnore
    @JsonProperty("customMessage")
    private String getCustomMessage() {
        return contactPolicy.getDescription();
    }
}
