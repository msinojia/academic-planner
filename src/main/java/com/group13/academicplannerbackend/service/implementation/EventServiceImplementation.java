package com.group13.academicplannerbackend.service.implementation;

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

        Schedule schedule = new Schedule();
        ZoneId atlanticTimeZone = ZoneId.of("America/Halifax");
        ZonedDateTime atlanticZonedDateTime = ZonedDateTime.now(atlanticTimeZone);
        LocalDateTime currentDateTime = atlanticZonedDateTime.toLocalDateTime();
        LocalDate currentDate = currentDateTime.toLocalDate();

        List<EventDTO> fixedEventDTOs = getFixedEvents(currentDate,variableEvent.getDeadline().toLocalDate(), principal);
        List<VariableEvent> variableEvents = variableEventRepository.findAllByUserEmail(principal.getName());
        List<EventDTO> variableEventDTOs = new ArrayList<>();
        for (VariableEvent varEvent : variableEvents) {
            EventDTO varEventDTO = modelMapper.map(variableEvent, EventDTO.class);
            varEventDTO.setStartDate(varEvent.getSchedule().getScheduledDateTime().toLocalDate());
            varEventDTO.setStartTime(varEvent.getSchedule().getScheduledDateTime().toLocalTime());
            varEventDTO.setEndDate(varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalDate());
            varEventDTO.setEndTime(varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalTime());
            varEventDTO.setReschedulable(true);
            varEventDTO.setEventType(EventType.VARIABLE);
            variableEventDTOs.add(varEventDTO);
        }

        fixedEventDTOs.addAll(variableEventDTOs);
        fixedEventDTOs.sort(Comparator.comparing(EventDTO::getStartDate).thenComparing(EventDTO::getStartTime));

        LocalDateTime scheduledDateTime = findStartTimeForVariableEvent(variableEvent, fixedEventDTOs, currentDateTime, variableEvent.getDeadline());
        schedule.setScheduledDateTime(scheduledDateTime);
        variableEvent.setSchedule(schedule);
        schedule.setVariableEvent(variableEvent);
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
            variableEvent.setUser(user);
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
        LocalDateTime firstDateTime = LocalDateTime.of(firstDate, LocalTime.MIN);
        LocalDateTime secondDateTime = LocalDateTime.of(secondDate, LocalTime.MAX);

        List<VariableEvent> variableEvents = variableEventRepository.findAllByStartDateOrEndDateBetweenDates(firstDateTime, secondDateTime, principal.getName());
        List<EventDTO> variableEventDTOs = new ArrayList<>();
        for (VariableEvent varEvent : variableEvents) {
            EventDTO varEventDTO = modelMapper.map(varEvent, EventDTO.class);
            varEventDTO.setStartDate(varEvent.getSchedule().getScheduledDateTime().toLocalDate());
            varEventDTO.setStartTime(varEvent.getSchedule().getScheduledDateTime().toLocalTime());
            varEventDTO.setEndDate(varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalDate());
            varEventDTO.setEndTime(varEvent.getSchedule().getScheduledDateTime().plus(varEvent.getDuration()).toLocalTime());
            varEventDTO.setReschedulable(true);
            varEventDTO.setEventType(EventType.VARIABLE);
            variableEventDTOs.add(varEventDTO);
        }

        eventDTOs.addAll(variableEventDTOs);
        return eventDTOs;
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<FixedEvent> findFixedEventById(Long id) {
        return fixedEventRepository.findById(id);
    }

    @Override
    public Optional<VariableEvent> findVariableEventById(Long id) {
        return variableEventRepository.findById(id);
    }

    /**
     * @return
     */
    public List<EventDTO> getFixedEvents(LocalDate firstDate, LocalDate secondDate, Principal principal) {
        List<FixedEvent> events = fixedEventRepository.findAllNonRepeatingByStartDateOrEndDateBetweenDates(firstDate, secondDate, principal.getName());
        List<FixedEvent> repeatingEvents = fixedEventRepository.findAllRepeatingByEndDateGreaterThanDate(firstDate, principal.getName());

        for(FixedEvent e : repeatingEvents) {
            RepititionType repititionType = e.getRepeatEvent().getRepititionType();

            int daysDifferenceBetweenStartAndEndDate = Period.between(e.getStartDate(), e.getEndDate()).getDays();
            LocalDate date1 = (e.getStartDate().isEqual(firstDate) || e.getStartDate().isAfter(firstDate)) ? e.getStartDate() : firstDate.minusDays(daysDifferenceBetweenStartAndEndDate);
            LocalDate date2 = (e.getRepeatEvent().getEndDate().isBefore(secondDate))
                    ? e.getRepeatEvent().getEndDate() : secondDate;

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

    private LocalDateTime findStartTimeForVariableEvent(VariableEvent variableEvent, List<EventDTO> fixedEventDTOs, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean slotFound = false;
        LocalDateTime potentialEndTime = LocalDateTime.MAX;
        while (!slotFound && startDateTime.isBefore(endDateTime)) {
            slotFound = true;

            for (EventDTO fixedEvent : fixedEventDTOs) {
                LocalDateTime fixedEventStart = LocalDateTime.of(fixedEvent.getStartDate(), fixedEvent.getStartTime());
                LocalDateTime fixedEventEnd = LocalDateTime.of(fixedEvent.getEndDate(), fixedEvent.getEndTime());

                potentialEndTime = startDateTime.plus(variableEvent.getDuration());

                if ((startDateTime.isEqual(fixedEventStart) || startDateTime.isAfter(fixedEventStart)) && startDateTime.isBefore(fixedEventEnd)
                        || (potentialEndTime.isAfter(fixedEventStart) && potentialEndTime.isBefore(fixedEventEnd))
                        || (startDateTime.isBefore(fixedEventStart) && potentialEndTime.isAfter(fixedEventEnd))
                ) {
                    startDateTime = fixedEventEnd;
                    slotFound = false;
                    break;
                }
            }
        }

        if (slotFound &&
                (potentialEndTime.isBefore(variableEvent.getDeadline()) || (potentialEndTime.isEqual(variableEvent.getDeadline())))
        ) {
            return startDateTime;
        } else {
            return null;
        }
    }
}
