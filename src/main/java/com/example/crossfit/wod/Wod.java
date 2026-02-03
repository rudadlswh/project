package com.example.crossfit.wod;

import com.example.crossfit.member.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "wods")
public class Wod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate wodDate;

    @Column(nullable = false)
    private String title;

    @Column
    private String type;

    @Lob
    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

    protected Wod() {
    }

    public Wod(LocalDate wodDate, String title, String type, String description, User createdBy) {
        this.wodDate = wodDate;
        this.title = title;
        this.type = type;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getWodDate() {
        return wodDate;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void update(String title, String type, String description) {
        this.title = title;
        this.type = type;
        this.description = description;
    }
}
