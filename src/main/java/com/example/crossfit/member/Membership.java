package com.example.crossfit.member;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer remainingCount;

    protected Membership() {
    }

    public Membership(User user, MembershipType type, LocalDate startDate, LocalDate endDate, Integer remainingCount) {
        this.user = user;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingCount = remainingCount;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MembershipType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }
}
