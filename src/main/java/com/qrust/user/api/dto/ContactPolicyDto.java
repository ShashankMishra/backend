package com.qrust.user.api.dto;

import lombok.Getter;

@Getter
public enum ContactPolicyDto {
    ALWAYS("Always available"),
    DAYTIME_ONLY("Daytime only, e.g. 8AM – 8PM"),
    NIGHTTIME_ONLY("Nighttime only, e.g. 8PM – 8AM"),
    DAYTIME_WEEKENDS_ONLY("Weekends only, e.g. Sat & Sun 8AM – 8PM"),
    NIGHTTIME_WEEKENDS_ONLY("Weekends only, e.g. Sat & Sun 8AM – 8PM"),
    WORK_HOURS("Weekdays only, e.g. Mon–Fri 8AM – 8PM"),
    NEVER("Never available");

    private String description;

    ContactPolicyDto(String description) {
        this.description = description;
    }
}
