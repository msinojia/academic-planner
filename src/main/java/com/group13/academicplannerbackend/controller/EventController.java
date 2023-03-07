package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.model.Event;
import com.group13.academicplannerbackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
public class EventController {
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @CrossOrigin
    @PostMapping
    public String createEvent(@RequestBody Event event) {
        eventService.createEvent(event);
        return "Event created successfully";
    }
}
