package com.group13.academicplannerbackend.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group13.academicplannerbackend.model.*;
import com.group13.academicplannerbackend.repository.*;
import com.group13.academicplannerbackend.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

//@SpringBootTest
public class EventServiceImplementationTest {
    @InjectMocks
    private EventServiceImplementation eventService;

    @Mock
    private FixedEventRepository fixedEventRepository;

    @Mock
    private VariableEventRepository variableEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFixedEvent() {
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setName("ASDC Classes");
        fixedEvent.setStartDate(LocalDate.now());
        fixedEvent.setStartTime(LocalTime.of(14, 30));
        fixedEvent.setEndDate(LocalDate.now());
        fixedEvent.setEndTime(LocalTime.of(17, 30));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");

        User user = new User();
        user.setEmail("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);

        eventService.createFixedEvent(fixedEvent, principal);
        verify(fixedEventRepository, times(1)).save(fixedEvent);
    }

    @Test
    public void updateFixedEvent_NotFound() {
        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(100l);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(new User());
        when(fixedEventRepository.findById(100l)).thenReturn(null);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_NotAuthorized() {
        User user = new User();
        user.setId(122L);
        User eventUser = new User();
        eventUser.setId(211L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(122L);
        fixedEvent.setUser(eventUser);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);
        when(fixedEventRepository.findById(1L)).thenReturn(fixedEvent);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_NotReschedulable() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setUser(user);
        fixedEvent.setReschedulable(false);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);
        when(fixedEventRepository.findById(1L)).thenReturn(fixedEvent);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_Success() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
        fixedEvent.setUser(user);
        fixedEvent.setReschedulable(true);
        fixedEvent.setRepeat(true);
        fixedEvent.setRepeatEvent(new RepeatEvent());

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);
        when(fixedEventRepository.findById(1L)).thenAnswer(invocation -> {
            if (invocation.getArgument(0).equals(1L)) {
                return fixedEvent;
            } else {
                throw new RuntimeException("Event not found");
            }
        });
        when(fixedEventRepository.save(any(FixedEvent.class))).thenReturn(fixedEvent);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.SUCCESS, result);
    }


}
