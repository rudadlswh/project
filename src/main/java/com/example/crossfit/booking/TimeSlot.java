package com.example.crossfit.booking;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public enum TimeSlot {
    SLOT_0900(LocalTime.of(9, 0)),
    SLOT_1030(LocalTime.of(10, 30)),
    SLOT_1730(LocalTime.of(17, 30)),
    SLOT_1900(LocalTime.of(19, 0)),
    SLOT_2030(LocalTime.of(20, 30));

    private final LocalTime time;

    TimeSlot(LocalTime time) {
        this.time = time;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDisplay() {
        return time.toString();
    }

    public static TimeSlot fromString(String value) {
        LocalTime parsed = LocalTime.parse(value);
        return Arrays.stream(values())
                .filter(slot -> slot.time.equals(parsed))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid time slot"));
    }

    public static List<TimeSlot> defaultSlots() {
        return Arrays.asList(values());
    }
}
