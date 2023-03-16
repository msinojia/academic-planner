package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.model.FixedEvent;
import com.group13.academicplannerbackend.model.UpdateEventStatus;
import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.VariableEvent;
import com.group13.academicplannerbackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> createFixedEvent(@RequestBody FixedEvent fixedEvent) {
        eventService.createFixedEvent(fixedEvent);
        return ResponseEntity.ok("FixedEvent created successfully");
    } 

    @CrossOrigin
    @PutMapping("/fixed") 
    public ResponseEntity<String> updateFixedEvent(@RequestBody FixedEvent fixedEvent) {
        UpdateEventStatus status = eventService.updateFixedEvent(fixedEvent);
        switch (status) {
            case SUCCESS:
                return ResponseEntity.ok("Event updated successfully");
            case NOT_RESCHEDULABLE:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event is not reschedulable");
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    } 

    @CrossOrigin
    @DeleteMapping("/fixed/{id}")
    public ResponseEntity<String> deleteFixedEventById(@PathVariable long id) {
        boolean status=eventService.deleteFixedEvent(id);
        if(status)
        {
            return ResponseEntity.ok("Event delete successfully");
        }
        else
        {
            return ResponseEntity.notFound().build();
        } 
    }

    @CrossOrigin
    @PostMapping("/variable")
    public ResponseEntity<String> createVariableEvent(@RequestBody VariableEvent variableEvent) {
        eventService.createVariableEvent(variableEvent);
        return ResponseEntity.ok("Variable Event created successfully");
    }

    @CrossOrigin
    @PutMapping("/variable")
    public ResponseEntity<String> updateVariableEvent(@RequestBody VariableEvent variableEvent) {
        UpdateEventStatus status=eventService.updateVariableEvent(variableEvent);
        return ResponseEntity.ok("Variable Event created successfully");
    }

    @CrossOrigin
    @DeleteMapping("/variable/{id}")
    public ResponseEntity<String> deleteVariableEventById(@PathVariable long id) {
        boolean status=eventService.deleteVariableEvent(id);
        if(status)
        {
            return ResponseEntity.ok("Event delete successfully");
        }
        else
        {
            return ResponseEntity.notFound().build();
        } 
    }

    @CrossOrigin
    @GetMapping
    public List<EventDTO> getEvents(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate firstDate,
                                    @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate secondDate) {
        return eventService.getEvents(firstDate, secondDate);
    }
}
