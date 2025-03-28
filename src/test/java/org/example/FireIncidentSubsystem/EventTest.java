package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", fault);
    }

    @Test
    void getTime() {
        assertEquals("12:12:12", event.getTime());
    }

    @Test
    void getZoneId() {
        assertEquals(2, event.getZoneId());
    }

    @Test
    void getEventType() {
        assertEquals(EventType.DRONE_REQUEST, event.getEventType());
    }

    @Test
    void getSeverityLevel() {
        assertEquals("High", event.getSeverityLevel());
    }

    @Test
    void getSeverityWaterAmount() {
        assertEquals(30, event.getSeverityWaterAmount());
    }
}