package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.Event;
import com.group13.academicplannerbackend.model.RepeatEvent;
import com.group13.academicplannerbackend.repository.EventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImplementation implements EventService {
    private EventRepository eventRepository;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * @param event
     */
    @Override
    public void createEvent(Event event) {
        if(event.isRepeat()) {
            RepeatEvent repeatEvent = event.getRepeatEvent();
            repeatEvent.setEvent(event);
            event.setRepeatEvent(repeatEvent);
        }
        eventRepository.save(event);
    }
}
