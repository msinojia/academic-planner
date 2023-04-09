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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
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
        fixedEvent.setId(1L);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(new User());
        when(fixedEventRepository.findById(1L)).thenReturn(null);

        UpdateEventStatus result = eventService.updateFixedEvent(fixedEvent, principal);
        assertEquals(UpdateEventStatus.NOT_FOUND, result);
    }

    @Test
    public void updateFixedEvent_NotAuthorized() {
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setId(1L);
        User eventUser = new User();
        eventUser.setEmail("pankti25@gmail.com");
        eventUser.setId(2L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setId(1L);
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
    public void deleteFixedEvent_notFound() {
        User user = new User();
        user.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.NOT_FOUND, result);
        verify(fixedEventRepository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteFixedEvent_notAuthorized() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setUser(user2);
        fixedEvent.setId(1L);

        Principal principal = () -> user1.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user1);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.of(fixedEvent));

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.NOT_AUTHORIZED, result);
        verify(fixedEventRepository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteFixedEvent_success() {
        User user = new User();
        user.setId(1L);

        FixedEvent fixedEvent = new FixedEvent();
        fixedEvent.setUser(user);
        fixedEvent.setId(1L);

        Principal principal = () -> user.getEmail();

        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        when(fixedEventRepository.findById(any(Long.class))).thenReturn(Optional.of(fixedEvent));

        DeleteEventStatus result = eventService.deleteFixedEvent(1L, principal);

        assertEquals(DeleteEventStatus.SUCCESS, result);
        verify(fixedEventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void createVariableEvent_UserNotFound() {
        VariableEvent variableEvent = new VariableEvent();
        variableEvent.setName("Exam");
        variableEvent.setDetails("Chapter 3 and 4");
        variableEvent.setEventPriority(EventPriority.HIGH);
        variableEvent.setEventCategory(EventCategory.GROUP_STUDY);
        variableEvent.setDuration(Duration.ofHours(2));
        variableEvent.setDeadline(LocalDateTime.of(2023, 4, 15, 12, 0));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(null);

        String result = eventService.createVariableEvent(variableEvent, principal).toString();
        assertEquals("[]", result);
    }

    @Test
    public void createVariableEvent_NullVariableEvent() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("pankti@gmail.com");

        List<EventDTO> result = eventService.createVariableEvent(null, principal);
        assertNull(result);
    }

    @Test
    public void testCreateVariableEvent() {
        Principal principal = mock(Principal.class);
        User user = new User();
        VariableEvent variableEvent = new VariableEvent();

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(principal.getName())).thenReturn(user);
        List<EventDTO> result = eventService.createVariableEvent(variableEvent, principal);

        assertNotNull(result);
        verify(variableEventRepository, times(1)).save(variableEvent);
        verify(userRepository, times(1)).findByEmail(principal.getName());
    }

    

}
