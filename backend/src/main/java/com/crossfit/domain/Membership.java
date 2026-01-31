package com.crossfit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "membership")
public class Membership extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
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

    public void setType(MembershipType type) {
        this.type = type;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }
}
