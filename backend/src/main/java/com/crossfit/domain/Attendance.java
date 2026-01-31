package com.crossfit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "att_date"})
})
public class Attendance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "att_date", nullable = false)
    private LocalDate date;

    protected Attendance() {
    }

    public Attendance(User user, LocalDate date) {
        this.user = user;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }
}
