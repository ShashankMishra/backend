package com.qrust.api.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonDetailsDto.class, name = "PERSON"),
        @JsonSubTypes.Type(value = VehicleDetailsDto.class, name = "VEHICLE"),
        @JsonSubTypes.Type(value = ChildDetailsDto.class, name = "CHILD"),
        @JsonSubTypes.Type(value = LuggageDetailsDto.class, name = "LUGGAGE"),
        @JsonSubTypes.Type(value = LockscreenDetailsDto.class, name = "LOCKSCREEN"),
})
public interface QRDetailsDto {}

