package com.qrust.common.domain.user;


import com.qrust.common.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String userId;
    private List<UserAddress> addresses = new ArrayList<>();
    private List<Contact> contacts = new ArrayList<>();

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void addContact(Contact contact) {
        // if contact already exist with same number then update the name
        for (Contact existingContact : contacts) {
            if (Objects.equals(existingContact.getPhoneNumber(), contact.getPhoneNumber())) {
                existingContact.setName(contact.getName());
                return; // Exit after updating the existing contact
            }
        }
        contacts.add(contact);
    }

}
