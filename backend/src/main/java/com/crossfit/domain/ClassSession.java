package com.crossfit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "class_session", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_date", "time_slot"})
})
public class ClassSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false, length = 20)
    private TimeSlot timeSlot;

    private Integer capacity;

    protected ClassSession() {
    }

    public ClassSession(LocalDate sessionDate, TimeSlot timeSlot, Integer capacity) {
        this.sessionDate = sessionDate;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
