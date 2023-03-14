package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.FixedEventRepository;
import com.group13.academicplannerbackend.repository.VariableEventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.apache.commons.lang3.SerializationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {
    private FixedEventRepository fixedEventRepository;
    private VariableEventRepository variableEventRepository;
    private ModelMapper modelMapper;

    @Autowired
    public EventServiceImplementation(FixedEventRepository fixedEventRepository,
                                      VariableEventRepository variableEventRepository,
                                      ModelMapper modelMapper) {
        this.fixedEventRepository = fixedEventRepository;
        this.variableEventRepository = variableEventRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param fixedEvent
     */
    @Override
    public void createFixedEvent(FixedEvent fixedEvent) {
        if(fixedEvent.isRepeat()) {
            RepeatEvent repeatEvent = fixedEvent.getRepeatEvent();
            repeatEvent.setEvent(fixedEvent);
            fixedEvent.setRepeatEvent(repeatEvent);
        }
        fixedEventRepository.save(fixedEvent);
    }

    /**
     * @return 
     */
    @Override
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate) {
        List<FixedEvent> events = fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate);
        List<FixedEvent> repeatingEvents = fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate);

        for(FixedEvent e : repeatingEvents) {
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
                    FixedEvent clonedFixedEvent = SerializationUtils.clone(e);
                    clonedFixedEvent.setStartDate(date1.plusDays(i));
                    clonedFixedEvent.setEndDate(date1.plusDays(i).plusDays(daysDifferenceBetweenStartAndEndDate));
                    events.add(clonedFixedEvent);
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
