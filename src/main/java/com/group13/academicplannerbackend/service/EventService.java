package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.Event;
import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.VariableEvent;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    void createEvent(Event event);
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate);

    void createVariableEvent(VariableEvent variableEvent);
}
