package org.example.FireIncidentSubsystem;

import org.example.DroneSystem.Drone;
import org.example.DroneSystem.DroneSubsystem;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    private Event event;
    private Drone drone;

    @BeforeEach
    void setUp() throws SocketException {
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT");
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
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

    @Test
    void getId() {
        assertEquals(1, event.getId());
    }

    @Test
    void testGetTime() {
        assertEquals("12:12:12", event.getTime());
    }

    @Test
    void testGetZoneId() {
        assertEquals(2, event.getZoneId());
    }

    @Test
    void testGetEventType() {
        assertEquals(EventType.DRONE_REQUEST, event.getEventType());
    }

    @Test
    void testGetSeverityLevel() {
        assertEquals("High", event.getSeverityLevel());
    }

    @Test
    void testGetSeverityWaterAmount() {
        assertEquals(30, event.getSeverityWaterAmount());
    }

    @Test
    void serialize() {
        assertEquals("1,12:12:12,2,DRONE_REQUEST,High,NO_FAULT", event.serialize());
    }

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

    @Test
    void isValidEvent() {
        assertTrue(event.isValidEvent(event));
    }

    @Test
    void testToString() {
        assertNotNull(event.toString());
    }

    @Test
    void getCurrentWaterAmountNeeded() {
        assertEquals(30, event.getCurrentWaterAmountNeeded());
    }

    @Test
    void setCurrentWaterAmountNeeded() {
        event.setCurrentWaterAmountNeeded(20);
        assertEquals(20, event.getCurrentWaterAmountNeeded());
    }

    @Test
    void getAssignedDrone() {
        assertNull(event.getAssignedDrone());
    }

    @Test
    void setAssignedDrone() {
        Drone drone = new Drone(1, 15, null, 0.1);
        event.setAssignedDrone(drone);
        assertEquals(drone, event.getAssignedDrone());
    }

    @Test
    void getFault() {
        assertEquals("NO_FAULT", event.getFault());
    }

    @Test
    void setFault() {
        event.setFault("FAULTY_SENSOR");
        assertEquals("FAULTY_SENSOR", event.getFault());
    }
}