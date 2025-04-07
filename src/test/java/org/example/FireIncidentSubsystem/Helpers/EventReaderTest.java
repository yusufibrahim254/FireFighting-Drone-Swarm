package org.example.FireIncidentSubsystem.Helpers;

import org.example.DroneSystem.DroneSubsystem;
import org.example.FireIncidentSubsystem.Event;
import org.example.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EventReader class to verify event reading functionality.
 */
class EventReaderTest {
    private static final String testFile = "docs/Sample_event_file.csv";

    /**
     * Test case to validate the reading of events from a CSV file.
     * It checks that events are read correctly and ensures that the events array is not empty.
     */
    @Test
    void readEvents() {
        Event[] events = EventReader.readEvents(testFile);

        assertNotNull(events);
        assertTrue(events.length > 0);

        Event firstEvent = events[0];
        assertNotNull(firstEvent);

        int middleIndex = events.length / 2;
        Event middleEvent = events[middleIndex];
        assertNotNull(middleEvent);

        Event lastEvent = events[events.length - 1];
        assertNotNull(lastEvent);
    }
}
