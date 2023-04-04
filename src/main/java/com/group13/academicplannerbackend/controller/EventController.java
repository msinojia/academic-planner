package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @CrossOrigin
    @PostMapping("/fixed")
    public ResponseEntity<String> createFixedEvent(@RequestBody FixedEvent fixedEvent, Principal principal) {
        eventService.createFixedEvent(fixedEvent, principal);
        return ResponseEntity.ok("FixedEvent created successfully");
    }

    @CrossOrigin
    @PutMapping("/fixed")
    public ResponseEntity<String> updateFixedEvent(@RequestBody FixedEvent fixedEvent, Principal principal) {
        UpdateEventStatus status = eventService.updateFixedEvent(fixedEvent, principal);
        switch (status) {
            case SUCCESS:
                return ResponseEntity.ok("Event updated successfully");
            case NOT_RESCHEDULABLE:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event is not reschedulable");
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            case NOT_AUTHORIZED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this event");
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }

    @CrossOrigin
    @DeleteMapping("/fixed/{id}")
    public ResponseEntity<String> deleteFixedEventById(@PathVariable long id, Principal principal) {
        DeleteEventStatus status = eventService.deleteFixedEvent(id, principal);
        switch (status) {
            case SUCCESS:
                return ResponseEntity.ok("Event deleted successfully");
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            case NOT_AUTHORIZED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this event");
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }

    @CrossOrigin
    @PostMapping("/variable")
    public ResponseEntity<String> createVariableEvent(@RequestBody VariableEvent variableEvent, Principal principal) {
        eventService.createVariableEvent(variableEvent, principal);
        return ResponseEntity.ok("Variable Event created successfully");
    }

    @CrossOrigin
    @PutMapping("/variable")
    public ResponseEntity<String> updateVariableEvent(@RequestBody VariableEvent variableEvent, Principal principal) {
        UpdateEventStatus status = eventService.updateVariableEvent(variableEvent, principal);
        switch (status) {
            case SUCCESS:
                return ResponseEntity.ok("Event updated successfully");
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            case NOT_AUTHORIZED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this event");
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }

    @CrossOrigin
    @DeleteMapping("/variable/{id}")
    public ResponseEntity<String> deleteVariableEventById(@PathVariable long id, Principal principal) {
        DeleteEventStatus status = eventService.deleteVariableEvent(id, principal);

        switch (status) {
            case SUCCESS:
                return ResponseEntity.ok("Event deleted successfully");
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            case NOT_AUTHORIZED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this event");
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }

    @CrossOrigin
    @GetMapping
    public List<EventDTO> getEvents(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate firstDate,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate secondDate,
                                    Principal principal) {
        return eventService.getEvents(firstDate, secondDate, principal);
    }
}
