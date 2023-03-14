package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.FixedEvent;
import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.VariableEvent;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    void createFixedEvent(FixedEvent fixedEvent);
    void createVariableEvent(VariableEvent variableEvent);
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate);
}
