package com.qrust.user.api.dto;

import lombok.Getter;

@Getter
public enum ContactPolicyDto {
    ALWAYS("Always available"),
    DAYTIME_ONLY("Day time only (8AM – 8PM)"),
    NIGHTTIME_ONLY("Night time only (8PM – 8AM)"),
    DAYTIME_WEEKENDS_ONLY("Weekends only (Sat & Sun 8AM – 8PM)"),
    NIGHTTIME_WEEKENDS_ONLY("Weekends only (Sat & Sun 8PM – 8AM)"),
    WORK_HOURS("Weekdays only (Mon–Fri 8AM – 8PM)"),
    NEVER("Never available");

    private String description;

    ContactPolicyDto(String description) {
        this.description = description;
    }
}
