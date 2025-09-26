package com.qrust.user.api.dto;

import lombok.Getter;

import java.time.Instant;

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

    public boolean isAvailableNow(Instant now) {
        // we need to make sure that we are checking time in IST India Standard Time

        int hour = now.atZone(java.time.ZoneId.of("Asia/Kolkata")).getHour();
        int dayOfWeek = now.atZone(java.time.ZoneId.of("Asia/Kolkata")).getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        switch (this) {
            case ALWAYS:
                return true;
            case DAYTIME_ONLY:
                return hour >= 8 && hour < 20;
            case NIGHTTIME_ONLY:
                return hour < 8 || hour >= 20;
            case DAYTIME_WEEKENDS_ONLY:
                return (dayOfWeek == 6 || dayOfWeek == 7) && (hour >= 8 && hour < 20);
            case NIGHTTIME_WEEKENDS_ONLY:
                return (dayOfWeek == 6 || dayOfWeek == 7) && (hour < 8 || hour >= 20);
            case WORK_HOURS:
                return dayOfWeek >= 1 && dayOfWeek <= 5 && (hour >= 8 && hour < 20);
            default:
                return false;
        }
    }
}
