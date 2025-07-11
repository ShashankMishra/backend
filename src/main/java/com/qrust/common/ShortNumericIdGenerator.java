package com.qrust.common;

import java.time.Instant;
import java.util.Random;

/**
 * Utility class to generate short, numeric-only, mostly-unique IDs.
 * Format: [EpochSeconds][3-digit random] → e.g. "1720609123482"
 */
public class ShortNumericIdGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Generates a short numeric ID using epoch time and random digits.
     *
     * @return A numeric-only unique string ID (typically 13 digits).
     */
    public static String generate() {
        long timestamp = Instant.now().getEpochSecond(); // 10-digit timestamp
        int random = RANDOM.nextInt(1000); // 3-digit random number (000–999)
        return String.format("%d%03d", timestamp, random);
    }
}

