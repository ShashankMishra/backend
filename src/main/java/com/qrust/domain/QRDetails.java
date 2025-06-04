package com.qrust.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public interface QRDetails {

    @SneakyThrows
    static QRDetails fromJson(String json) {
        var mapper = new ObjectMapper();
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
        return new ObjectMapper().writeValueAsString(this);
    }
}
