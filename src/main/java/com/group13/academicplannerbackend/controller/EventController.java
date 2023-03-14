package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.model.FixedEvent;
import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.VariableEvent;
import com.group13.academicplannerbackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @CrossOrigin
    @PostMapping("/fixed")
    public String createFixedEvent(@RequestBody FixedEvent fixedEvent) {
        eventService.createFixedEvent(fixedEvent);
        return "FixedEvent created successfully";
    }

    @CrossOrigin
    @PostMapping("/variable")
    public String createVariableEvent(@RequestBody VariableEvent variableEvent) {
        eventService.createVariableEvent(variableEvent);
        return "Variable Event created successfully";
    }

    @CrossOrigin
    @GetMapping
    public List<EventDTO> getEvents(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate firstDate,
                                    @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate secondDate) {
        return eventService.getEvents(firstDate, secondDate);
    }
}
