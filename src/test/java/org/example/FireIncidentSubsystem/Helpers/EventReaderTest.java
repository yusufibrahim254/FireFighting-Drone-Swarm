package org.example.FireIncidentSubsystem.Helpers;

import org.example.DroneSubsystem;
import org.example.FireIncidentSubsystem.Event;
import org.example.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventReaderTest {
    private Scheduler scheduler;
    private static final String testFile = "Sample_event_file.csv";

    @BeforeEach
    void setUp(){
        DroneSubsystem droneSubsystem = new DroneSubsystem();
        scheduler = new Scheduler(droneSubsystem);
    }

    @Test
    void readEvents() {
        EventReader.readEvents(testFile, scheduler);

        assertFalse(scheduler.isEmpty());
        Event testEvent = scheduler.getEvent(); // just one event to verify
        assertEquals("14:03:15", testEvent.getTime());
        assertEquals(3, testEvent.getZoneId());
        assertEquals(EventType.FIRE_DETECTED, testEvent.getEventType());
        assertEquals("High", testEvent.getSeverityLevel());

    }
}