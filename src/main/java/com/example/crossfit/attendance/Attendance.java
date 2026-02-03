package com.example.crossfit.attendance;

import com.example.crossfit.member.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "attended_date"}))
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDate attendedDate;

    protected Attendance() {
    }

    public Attendance(User user, LocalDate attendedDate) {
        this.user = user;
        this.attendedDate = attendedDate;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getAttendedDate() {
        return attendedDate;
    }
}
