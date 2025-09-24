package com.qrust.common.domain;

import com.qrust.user.api.dto.ContactPolicyDto;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class ContactPreference {
    private ContactPolicyDto contactPolicy = ContactPolicyDto.ALWAYS;
    private String customMessage = contactPolicy.getDescription();
}
