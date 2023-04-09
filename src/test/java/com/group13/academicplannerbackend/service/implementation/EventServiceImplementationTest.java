package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.EventDTO;
import com.group13.academicplannerbackend.model.FixedEvent;
import com.group13.academicplannerbackend.model.RepeatEvent;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.repository.FixedEventRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VariableEventRepository;
import com.group13.academicplannerbackend.service.implementation.EventServiceImplementation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
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

    @Mock
    private Principal principal;

    private User user;
    private FixedEvent fixedEvent;

    @Before
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");

        fixedEvent = new FixedEvent();
        fixedEvent.setRepeat(true);
        RepeatEvent repeatEvent = new RepeatEvent();
        fixedEvent.setRepeatEvent(repeatEvent);
    }

    @Test
    public void testCreateFixedEvent() {
        Mockito.when(principal.getName()).thenReturn(user.getEmail());
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(fixedEventRepository.save(any(FixedEvent.class))).thenReturn(fixedEvent);

        List<EventDTO> expectedEvents = Collections.singletonList(new EventDTO());
        Mockito.when(eventService.rescheduleVariableEvents(principal)).thenReturn(expectedEvents);

        List<EventDTO> returnedEvents = eventService.createFixedEvent(fixedEvent, principal);

        assertEquals(expectedEvents, returnedEvents);
        Mockito.verify(fixedEventRepository, Mockito.times(1)).save(any(FixedEvent.class));
    }
}
