package com.crossfit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "wod", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wod_date"})
})
public class Wod extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wod_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    protected Wod() {
    }

    public Wod(LocalDate date, String title, String description, User createdBy) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
