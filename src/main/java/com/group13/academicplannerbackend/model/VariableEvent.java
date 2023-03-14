package com.group13.academicplannerbackend.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class VariableEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String details;

    @Enumerated(EnumType.STRING)
    private EventPriority eventPriority;

    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    private Duration duration;

    private LocalDateTime deadline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public EventPriority getEventPriority() {
        return eventPriority;
    }

    public void setEventPriority(EventPriority eventPriority) {
        this.eventPriority = eventPriority;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
