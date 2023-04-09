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
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class EventServiceImplementationTest {
    @InjectMocks
    private EventServiceImplementation eventService;

    @Mock
    private FixedEventRepository fixedEventRepository;

    @Mock
    private VariableEventRepository variableEventRepository;

    @Mock
    private UserRepository userRepository;

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
        user.setEmail("tpankti@gmail.com");
        when(userRepository.findByEmail("pankti@gmail.com")).thenReturn(user);

        eventService.createFixedEvent(fixedEvent, principal);
        verify(fixedEventRepository, times(1)).save(fixedEvent);
    }
}
