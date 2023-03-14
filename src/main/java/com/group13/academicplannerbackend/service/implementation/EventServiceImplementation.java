package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.Event;
import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.RepeatEvent;
import com.group13.academicplannerbackend.repository.EventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {
    private EventRepository eventRepository;
    private ModelMapper modelMapper;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
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

    /**
     * @return 
     */
    @Override
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate) {
//        List<Event> events = eventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate);
        List<Event> events = eventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate);
        List<EventDTO> eventDTOs = events.stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .collect(Collectors.toList());
        return eventDTOs;
    }
}
