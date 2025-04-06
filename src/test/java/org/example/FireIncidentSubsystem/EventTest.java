package org.example.FireIncidentSubsystem;

import org.example.DroneSystem.Drone;
import org.example.DroneSystem.DroneSubsystem;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class to validate its functionality, serialization, and validation methods.
 */
class EventTest {
    private Event event;
    private Drone drone;

    /**
     * Initializes the test instances of Event and Drone for testing.
     */
    @BeforeEach
    void setUp() throws SocketException {
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT");
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
    }

    /**
     * Test case to validate the time of the event.
     * Verifies that the time returned matches the expected value.
     */
    @Test
    void getTime() {
        assertEquals("12:12:12", event.getTime());
    }

    /**
     * Test case to validate the zone ID of the event.
     * Verifies that the zone ID returned matches the expected value.
     */
    @Test
    void getZoneId() {
        assertEquals(2, event.getZoneId());
    }

    /**
     * Test case to validate the event type of the event.
     * Verifies that the event type returned matches the expected value.
     */
    @Test
    void getEventType() {
        assertEquals(EventType.DRONE_REQUEST, event.getEventType());
    }

    /**
     * Test case to validate the severity level of the event.
     * Verifies that the severity level returned matches the expected value.
     */
    @Test
    void getSeverityLevel() {
        assertEquals("High", event.getSeverityLevel());
    }

    /**
     * Test case to validate the severity water amount.
     * Verifies that the water amount returned for the event's severity matches the expected value.
     */
    @Test
    void getSeverityWaterAmount() {
        assertEquals(30, event.getSeverityWaterAmount());
    }

    /**
     * Test case to validate the event ID.
     * Verifies that the event ID returned matches the expected value.
     */
    @Test
    void getId() {
        assertEquals(1, event.getId());
    }

    /**
     * Test case to validate the time of the event.
     * Verifies that the time returned matches the expected value.
     */
    @Test
    void testGetTime() {
        assertEquals("12:12:12", event.getTime());
    }

    /**
     * Test case to validate the zone ID of the event.
     * Verifies that the zone ID returned matches the expected value.
     */
    @Test
    void testGetZoneId() {
        assertEquals(2, event.getZoneId());
    }

    /**
     * Test case to validate the event type of the event.
     * Verifies that the event type returned matches the expected value.
     */
    @Test
    void testGetEventType() {
        assertEquals(EventType.DRONE_REQUEST, event.getEventType());
    }

    /**
     * Test case to validate the severity level of the event.
     * Verifies that the severity level returned matches the expected value.
     */
    @Test
    void testGetSeverityLevel() {
        assertEquals("High", event.getSeverityLevel());
    }

    /**
     * Test case to validate the severity water amount.
     * Verifies that the water amount returned for the event's severity matches the expected value.
     */
    @Test
    void testGetSeverityWaterAmount() {
        assertEquals(30, event.getSeverityWaterAmount());
    }

    /**
     * Test case to test the serialization of the event.
     * Verifies that the serialized event matches the expected format.
     */
    @Test
    void serialize() {
        assertEquals("1,12:12:12,2,DRONE_REQUEST,High,NO_FAULT", event.serialize());
    }

    /**
     * Test case to test the deserialization of the event.
     * Verifies that the deserialized event matches the expected values.
     */
    @Test
    void deserialize() {
        Event deserializedEvent = Event.deserialize("1,12:12:12,2,DRONE_REQUEST,High,NO_FAULT");
        assertEquals(1, deserializedEvent.getId());
        assertEquals("12:12:12", deserializedEvent.getTime());
        assertEquals(2, deserializedEvent.getZoneId());
        assertEquals(EventType.DRONE_REQUEST, deserializedEvent.getEventType());
        assertEquals("High", deserializedEvent.getSeverityLevel());
        assertEquals("NO_FAULT", deserializedEvent.getFault());
    }

    /**
     * Test case to validate that the event is valid.
     * Verifies that the event is valid by calling the isValidEvent method.
     */
    @Test
    void isValidEvent() {
        assertTrue(event.isValidEvent(event));
    }

    /**
     * Test case for the toString() method of the event.
     * Verifies that the toString() method does not return null.
     */
    @Test
    void testToString() {
        assertNotNull(event.toString());
    }

    /**
     * Test case to validate the current water amount needed for the event.
     * Verifies that the current water amount needed is returned correctly.
     */
    @Test
    void getCurrentWaterAmountNeeded() {
        assertEquals(30, event.getCurrentWaterAmountNeeded());
    }

    /**
     * Test case to set and get the current water amount needed for the event.
     * Verifies that the water amount can be set and retrieved correctly.
     */
    @Test
    void setCurrentWaterAmountNeeded() {
        event.setCurrentWaterAmountNeeded(20);
        assertEquals(20, event.getCurrentWaterAmountNeeded());
    }

    /**
     * Test case to get the assigned drone for the event.
     * Verifies that initially, the event does not have an assigned drone.
     */
    @Test
    void getAssignedDrone() {
        assertNull(event.getAssignedDrone());
    }

    /**
     * Test case to set and get the assigned drone for the event.
     * Verifies that a drone can be assigned and retrieved correctly.
     */
    @Test
    void setAssignedDrone() {
        Drone drone = new Drone(1, 15, null, 0.1);
        event.setAssignedDrone(drone);
        assertEquals(drone, event.getAssignedDrone());
    }

    /**
     * Test case to get the fault status of the event.
     * Verifies that the fault is returned correctly.
     */
    @Test
    void getFault() {
        assertEquals("NO_FAULT", event.getFault());
    }

    /**
     * Test case to set the fault status of the event.
     * Verifies that the fault status can be set correctly.
     */
    @Test
    void setFault() {
        event.setFault("FAULTY_SENSOR");
        assertEquals("FAULTY_SENSOR", event.getFault());
    }
}
