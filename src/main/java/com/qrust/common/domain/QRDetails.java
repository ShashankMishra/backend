package com.qrust.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.qrust.common.JsonUtil;
import lombok.SneakyThrows;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VehicleDetails.class, name = "VEHICLE"),
        @JsonSubTypes.Type(value = PersonDetails.class, name = "PERSON"),
        @JsonSubTypes.Type(value = ChildDetails.class, name = "CHILD"),
        @JsonSubTypes.Type(value = LuggageDetails.class, name = "LUGGAGE"),
        @JsonSubTypes.Type(value = LockscreenDetails.class, name = "LOCKSCREEN")
})
public interface QRDetails {

    @SneakyThrows
    static QRDetails fromJson(String json) {
        var mapper = JsonUtil.createMapper();
        JsonNode node = mapper.readTree(json);
        QRType type = QRType.valueOf(node.get("type").asText());

        return switch (type) {
            case VEHICLE -> mapper.treeToValue(node, VehicleDetails.class);
            case CHILD -> mapper.treeToValue(node, ChildDetails.class);
            case LUGGAGE -> mapper.treeToValue(node, LuggageDetails.class);
            case LOCKSCREEN -> mapper.treeToValue(node, LockscreenDetails.class);
            case PERSON -> mapper.treeToValue(node, PersonDetails.class);
        };
    }

    @SneakyThrows
    default String toJson() {
        return JsonUtil.createMapper().writeValueAsString(this);
    }
}
