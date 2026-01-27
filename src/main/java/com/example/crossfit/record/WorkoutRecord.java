package com.example.crossfit.record;

import com.example.crossfit.member.User;
import com.example.crossfit.wod.Wod;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "records")
public class WorkoutRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Wod wod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected WorkoutRecord() {
    }

    public WorkoutRecord(User user, Wod wod, RecordType type, String value) {
        this.user = user;
        this.wod = wod;
        this.type = type;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Wod getWod() {
        return wod;
    }

    public RecordType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
