package com.group13.academicplannerbackend.service.implementation;

import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy.FirstCharBasedValidator;
import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.FixedEventRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VariableEventRepository;
import com.group13.academicplannerbackend.service.EventService;
import org.apache.commons.lang3.SerializationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImplementation implements EventService {
    private FixedEventRepository fixedEventRepository;
    private VariableEventRepository variableEventRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    public EventServiceImplementation(FixedEventRepository fixedEventRepository,
                                      VariableEventRepository variableEventRepository,
                                      UserRepository userRepository,
                                      ModelMapper modelMapper) {
        this.fixedEventRepository = fixedEventRepository;
        this.variableEventRepository = variableEventRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param fixedEvent
     */
    @Override                                          
    public void createFixedEvent(FixedEvent fixedEvent, Principal principal) {
        if(fixedEvent.isRepeat()) {
            RepeatEvent repeatEvent = fixedEvent.getRepeatEvent();
            repeatEvent.setEvent(fixedEvent);
            fixedEvent.setRepeatEvent(repeatEvent);
        }
        User user = userRepository.findByEmail(principal.getName());
        fixedEvent.setUser(user);
        fixedEventRepository.save(fixedEvent);
    }

    @Override                                        
    public UpdateEventStatus updateFixedEvent(FixedEvent fixedEvent, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        FixedEvent checkInsideDB=fixedEventRepository.findById(fixedEvent.getId()).orElse(null);
        if(checkInsideDB!=null )
        {
            // Check if the user is authorized to update the event
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return UpdateEventStatus.NOT_AUTHORIZED;
            }

            if(checkInsideDB.isReschedulable())
            {
                if(fixedEvent.isRepeat()) {
                    RepeatEvent repeatEvent = fixedEvent.getRepeatEvent();
                    repeatEvent.setEvent(fixedEvent);
                    fixedEvent.setRepeatEvent(repeatEvent);
                    fixedEvent.setUser(user);
                }
                fixedEventRepository.save(fixedEvent);
                return UpdateEventStatus.SUCCESS;
            }
            else{
                return UpdateEventStatus.NOT_RESCHEDULABLE;
            }
        }
        else
        {
            return UpdateEventStatus.NOT_FOUND;
        }
    }

    @Override                                          
    public DeleteEventStatus deleteFixedEvent(Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        FixedEvent checkInsideDB=fixedEventRepository.findById(id).orElse(null);
        if(checkInsideDB!=null)
        {
            // Check if the user is authorized to update the event
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return DeleteEventStatus.NOT_AUTHORIZED;
            }

            fixedEventRepository.deleteById(id);
            return DeleteEventStatus.SUCCESS;
        }
        else
        {
            return DeleteEventStatus.NOT_FOUND;
        }
    }

    /**
     * @param variableEvent
     */
    @Override
    public void createVariableEvent(VariableEvent variableEvent, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        variableEvent.setUser(user);
        variableEventRepository.save(variableEvent);
    }

    @Override
    public UpdateEventStatus updateVariableEvent(VariableEvent variableEvent, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        VariableEvent checkInsideDB=variableEventRepository.findById(variableEvent.getId()).orElse(null);
        if(checkInsideDB!=null)
        {
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return UpdateEventStatus.NOT_AUTHORIZED;
            }
            variableEventRepository.save(variableEvent);
            return UpdateEventStatus.SUCCESS;
        }
        else
        {
            return UpdateEventStatus.NOT_FOUND;
        }
       
    }

    @Override                                          
    public DeleteEventStatus deleteVariableEvent(Long id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        VariableEvent checkInsideDB=variableEventRepository.findById(id).orElse(null);
        if(checkInsideDB!=null)
        {
            if (!user.getId().equals(checkInsideDB.getUser().getId())) {
                return DeleteEventStatus.NOT_AUTHORIZED;
            }
            variableEventRepository.deleteById(id);
            return DeleteEventStatus.SUCCESS;
        }
        else
        {
            return DeleteEventStatus.NOT_FOUND;
        }
      
    }

    /**
     * @return
     */
    @Override
    public List<EventDTO> getEvents(LocalDate firstDate, LocalDate secondDate, Principal principal) {
        List<EventDTO> eventDTOs = getFixedEvents(firstDate, secondDate, principal);
        List<VariableEvent> variableEvents = variableEventRepository.findAllByUserEmail(principal.getName());

        // Sort variable events by deadline
        variableEvents.sort(Comparator.comparing(VariableEvent::getDeadline));

        // Find free time slots and schedule variable events
        for (VariableEvent variableEvent : variableEvents) {
            LocalDateTime scheduledStartTime = findStartTimeForVariableEvent(variableEvent, eventDTOs, firstDate, secondDate);
            if (scheduledStartTime != null) {
                EventDTO scheduledVariableEvent = modelMapper.map(variableEvent, EventDTO.class);
                scheduledVariableEvent.setStartDate(scheduledStartTime.toLocalDate());
                scheduledVariableEvent.setStartTime(scheduledStartTime.toLocalTime());
                scheduledVariableEvent.setEndDate(scheduledStartTime.plus(variableEvent.getDuration()).toLocalDate());
                scheduledVariableEvent.setEndTime(scheduledStartTime.plus(variableEvent.getDuration()).toLocalTime());
                scheduledVariableEvent.setReschedulable(true);
                scheduledVariableEvent.setEventType(EventType.VARIABLE);
                eventDTOs.add(scheduledVariableEvent);
            }
        }

        return eventDTOs;
    }

    /**
     * @return
     */
    public List<EventDTO> getFixedEvents(LocalDate firstDate, LocalDate secondDate, Principal principal) {
        List<FixedEvent> events = fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate, principal.getName());
        List<FixedEvent> repeatingEvents = fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate, principal.getName());

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
                .peek(eventDTO -> eventDTO.setEventType(EventType.FIXED))
                .collect(Collectors.toList());
        return eventDTOs;
    }

    private LocalDateTime findStartTimeForVariableEvent(VariableEvent variableEvent, List<EventDTO> fixedEventDTOs, LocalDate firstDate, LocalDate secondDate) {
        LocalDateTime startTime = LocalDateTime.of(firstDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(secondDate, LocalTime.MAX);
        boolean slotFound = false;

        while (!slotFound && startTime.isBefore(endTime)) {
            slotFound = true;

            for (EventDTO fixedEvent : fixedEventDTOs) {
                LocalDateTime fixedEventStart = LocalDateTime.of(fixedEvent.getStartDate(), fixedEvent.getStartTime());
                LocalDateTime fixedEventEnd = LocalDateTime.of(fixedEvent.getEndDate(), fixedEvent.getEndTime());

                LocalDateTime potentialEndTime = startTime.plus(variableEvent.getDuration());

                if ((startTime.isEqual(fixedEventStart) || startTime.isAfter(fixedEventStart)) && startTime.isBefore(fixedEventEnd)
                        || (potentialEndTime.isAfter(fixedEventStart) && potentialEndTime.isBefore(fixedEventEnd))
                        || (startTime.isBefore(fixedEventStart) && potentialEndTime.isAfter(fixedEventEnd))) {
                    startTime = fixedEventEnd;
                    slotFound = false;
                    break;
                }
            }
        }

        if (slotFound) {
            return startTime;
        } else {
            return null;
        }
    }
}
