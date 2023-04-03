package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface EventService {
    void createFixedEvent(FixedEvent fixedEvent, Principal principal);
    UpdateEventStatus updateFixedEvent(FixedEvent fixedEvent, Principal principal);
    DeleteEventStatus deleteFixedEvent (Long id, Principal principal);
    void createVariableEvent(VariableEvent variableEvent, Principal principal);
    UpdateEventStatus updateVariableEvent(VariableEvent variableEvent, Principal principal);
    DeleteEventStatus deleteVariableEvent (Long id, Principal principal);
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate, Principal principal);
}
