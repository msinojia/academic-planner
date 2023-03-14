package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.EventRepository;
import com.group13.academicplannerbackend.repository.VariableEventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.apache.commons.lang3.SerializationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {
    private EventRepository eventRepository;
    private VariableEventRepository variableEventRepository;
    private ModelMapper modelMapper;

    @Autowired
    public EventServiceImplementation(EventRepository eventRepository,
                                      VariableEventRepository variableEventRepository,
                                      ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.variableEventRepository = variableEventRepository;
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
        List<Event> events = eventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate);
        List<Event> repeatingEvents = eventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate);

        for(Event e : repeatingEvents) {
            RepititionType repititionType = e.getRepeatEvent().getRepititionType();

            LocalDate date1 = (e.getStartDate().isAfter(firstDate)) ? e.getStartDate() : firstDate;
            LocalDate date2 = (e.getRepeatEvent().getEndDate().isBefore(secondDate))
                    ? e.getRepeatEvent().getEndDate() : secondDate;

            int daysDifferenceBetweenStartAndEndDate = Period.between(e.getStartDate(), e.getEndDate()).getDays();

            int daysDifferenceBetweenDate1AndDate2 = Period.between(date1, date2).getDays();

            for (int i = 0; i <= daysDifferenceBetweenDate1AndDate2; i++) {
                if(repititionType == RepititionType.DAILY
                        || (repititionType == RepititionType.WEEKLY
                        && e.getRepeatEvent().getWeeklyRepeatDays()[date1.plusDays(i).getDayOfWeek().getValue()%7])
                ) {
                    Event clonedEvent = SerializationUtils.clone(e);
                    clonedEvent.setStartDate(date1.plusDays(i));
                    clonedEvent.setEndDate(date1.plusDays(i).plusDays(daysDifferenceBetweenStartAndEndDate));
                    events.add(clonedEvent);
                }
                }
        }

        List<EventDTO> eventDTOs = events.stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .collect(Collectors.toList());
        return eventDTOs;
    }

    /**
     * @param variableEvent
     */
    @Override
    public void createVariableEvent(VariableEvent variableEvent) {
        variableEventRepository.save(variableEvent);
    }
}
