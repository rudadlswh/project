package com.crossfit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "record")
public class Record extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wod_id")
    private Wod wod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordType type;

    @Column(name = "record_value", nullable = false, length = 100)
    private String value;

    @Column(nullable = false)
    private LocalDate recordDate;

    protected Record() {
    }

    public Record(User user, Wod wod, RecordType type, String value, LocalDate recordDate) {
        this.user = user;
        this.wod = wod;
        this.type = type;
        this.value = value;
        this.recordDate = recordDate;
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

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
