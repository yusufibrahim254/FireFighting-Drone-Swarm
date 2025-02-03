package org.example;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {
    private Drone drone;

    @BeforeEach
    void setUp() {
        drone = new Drone(1,100.00);
    }

    @Test
    void processEvent() {
        Event event = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        drone.processEvent(event, 10);
        assertEquals(90.0, drone.getAgentCapacity(), "Agent capacity should decrease by 50L");
        assertEquals(DroneState.IDLE, drone.getState(), "DroneState should return to IDLE after processing is complete");
    }

    @Test
    void openBayDoors()throws InterruptedException {
        assertFalse(drone.isBayDoorOpen(), "Bay Doors should not be open, initially");
        drone.openBayDoors();
        assertTrue(drone.isBayDoorOpen(), "Bay Doors should be open");
    }

    @Test
    void closeBayDoors() throws InterruptedException {
        drone.openBayDoors();
        assertTrue(drone.isBayDoorOpen(), "Bay Doors should be open, initially");
        drone.closeBayDoors();
        assertFalse(drone.isBayDoorOpen(), "Bay doors should be closed");
    }

    @Test
    void refill() {
        drone.processEvent(new Event("20:00", 1, EventType.FIRE_DETECTED, "High"), 10);
        drone.refill();
        assertEquals(100.0, drone.getAgentCapacity(), "Drone should be refilled to max capacity");
        assertEquals(DroneState.REFILLING, drone.getState(), "Drone state should be 'REFILLING' during refill");
    }

    @Test
    void getState() {
        drone.setState(DroneState.EN_ROUTE);
        assertEquals(DroneState.EN_ROUTE, drone.getState(), "getState should return the current state.");
    }

    @Test
    void getAgentCapacity() {
        assertEquals(100.0, drone.getAgentCapacity(), "getAgentCapacity should return the initial full capacity.");
    }

    @Test
    void setState() {
        drone.setState(DroneState.FAULTED);
        assertEquals(DroneState.FAULTED, drone.getState(), "setState should return the current state.");
    }
}