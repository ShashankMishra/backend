package com.qrust.common.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.qrust.common.JsonUtil;
import lombok.SneakyThrows;

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
