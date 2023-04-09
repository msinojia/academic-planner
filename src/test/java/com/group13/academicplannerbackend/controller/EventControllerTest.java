package com.group13.academicplannerbackend.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group13.academicplannerbackend.model.*;
import org.apache.tomcat.util.http.parser.MediaType;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.*;
import com.group13.academicplannerbackend.controller.*;

import com.group13.academicplannerbackend.service.EventService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    void testUpdateFixedEvent_Success() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.SUCCESS;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@example.com");
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.ok("Event updated successfully"), response);
    }

    @Test
    void testUpdateFixedEvent_NotReschedulable() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.NOT_RESCHEDULABLE;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event is not reschedulable"), response);
    }

    @Test
    void testUpdateFixedEvent_NotFound() {
        // Given
        FixedEvent fixedEvent = new FixedEvent();
        UpdateEventStatus status = UpdateEventStatus.NOT_FOUND;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.updateFixedEvent(fixedEvent, mockPrincipal)).thenReturn(status);

        // When
        ResponseEntity<String> response = eventController.updateFixedEvent(fixedEvent, mockPrincipal);

        // Then
        verify(eventService).updateFixedEvent(fixedEvent, mockPrincipal);
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    public void testDeleteFixedEventByIdSuccess() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(anyLong(), any(Principal.class))).thenReturn(DeleteEventStatus.SUCCESS);

        ResponseEntity<String> response = eventController.deleteFixedEventById(1, mockPrincipal);

        assertAll("Response",
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertEquals("Event deleted successfully", response.getBody(), "Response body should match")
        );
    }

    @Test
    public void testDeleteFixedEventByIdNotFound() throws Exception {
        long eventId = 1;
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(eventService.deleteFixedEvent(eventId, mockPrincipal)).thenReturn(DeleteEventStatus.NOT_FOUND);

        mockMvc.perform(delete("/fixed/{id}", eventId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateVariableEvent_Success() {
        // Given
        VariableEvent variableEvent = new VariableEvent();
        Principal mockPrincipal = Mockito.mock(Principal.class);
        List<EventDTO> emptyUnscheduledEvents = Collections.emptyList();
        when(eventService.createVariableEvent(variableEvent, mockPrincipal)).thenReturn(emptyUnscheduledEvents);

        // When
        ResponseEntity<Map> response = eventController.createVariableEvent(variableEvent, mockPrincipal);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals("Variable Event created successfully", responseBody.get("message"));
        verify(eventService, times(1)).createVariableEvent(variableEvent, mockPrincipal);
    }

    @Test
    public void testUpdateVariableEvent_Success() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Call the updateVariableEvent method with a valid VariableEvent object and mock Principal
        VariableEvent variableEvent = new VariableEvent();
        UpdateEventStatus updateEventStatus = UpdateEventStatus.SUCCESS;
        when(eventService.updateVariableEvent(variableEvent, principal)).thenReturn(updateEventStatus);

        ResponseEntity<String> response = eventController.updateVariableEvent(variableEvent, principal);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event updated successfully", response.getBody());
        verify(eventService, times(1)).updateVariableEvent(variableEvent, principal);
    }

    @Test
    public void testDeleteVariableEventById_Success() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Mock the eventService to return SUCCESS for successful deletion
        when(eventService.deleteVariableEvent(anyLong(), eq(principal))).thenReturn(DeleteEventStatus.SUCCESS);

        // Call the deleteVariableEventById method with a valid event ID and mock Principal
        ResponseEntity<String> response = eventController.deleteVariableEventById(1, principal);

        // Assert that the response has an OK status and the correct message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event deleted successfully", response.getBody());
        verify(eventService, times(1)).deleteVariableEvent(1L, principal);
    }

    @Test
    public void testDeleteVariableEventById_NotFound() {
        // Create a mock Principal object for the authenticated user
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");

        // Mock the eventService to return NOT_FOUND for non-existent event ID
        when(eventService.deleteVariableEvent(anyLong(), eq(principal))).thenReturn(DeleteEventStatus.NOT_FOUND);

        // Call the deleteVariableEventById method with a non-existent event ID and mock Principal
        ResponseEntity<String> response = eventController.deleteVariableEventById(9, principal);

        // Assert that the response has a Not Found status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).deleteVariableEvent(9L, principal);
    }
}
