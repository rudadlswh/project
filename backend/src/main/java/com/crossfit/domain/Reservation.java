package com.crossfit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "session_id"})
})
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private ClassSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    private Integer waitlistPosition;

    protected Reservation() {
    }

    public Reservation(User user, ClassSession session, ReservationStatus status, Integer waitlistPosition) {
        this.user = user;
        this.session = session;
        this.status = status;
        this.waitlistPosition = waitlistPosition;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ClassSession getSession() {
        return session;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Integer getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }
}
