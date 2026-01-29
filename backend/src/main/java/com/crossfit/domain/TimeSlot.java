package com.crossfit.domain;

import java.time.LocalTime;

public enum TimeSlot {
    SLOT_09_00(LocalTime.of(9, 0)),
    SLOT_10_30(LocalTime.of(10, 30)),
    SLOT_17_30(LocalTime.of(17, 30)),
    SLOT_19_00(LocalTime.of(19, 0)),
    SLOT_20_30(LocalTime.of(20, 30));

    private final LocalTime time;

    TimeSlot(LocalTime time) {
        this.time = time;
    }

    public LocalTime getTime() {
        return time;
    }
}
