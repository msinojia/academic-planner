package com.group13.academicplannerbackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @JsonProperty
    private boolean isReschedulable;
    private String details;
    @Enumerated(EnumType.STRING)
    private EventPriority eventPriority;
    @JsonProperty
    private boolean isRepeat;
    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private RepeatEvent repeatEvent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isReschedulable() {
        return isReschedulable;
    }

    public void setReschedulable(boolean reschedulable) {
        isReschedulable = reschedulable;
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

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepeatEvent getRepeatEvent() {
        return repeatEvent;
    }

    public void setRepeatEvent(RepeatEvent repeatEvent) {
        this.repeatEvent = repeatEvent;
    }
}
