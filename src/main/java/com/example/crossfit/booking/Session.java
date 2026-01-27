package com.example.crossfit.booking;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlot timeSlot;

    @Column
    private Integer capacity;

    @Column(nullable = false)
    private LocalDateTime cutoffAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Session() {
    }

    public Session(LocalDate sessionDate, TimeSlot timeSlot, Integer capacity, LocalDateTime cutoffAt) {
        this.sessionDate = sessionDate;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.cutoffAt = cutoffAt;
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

    public LocalDateTime getCutoffAt() {
        return cutoffAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
