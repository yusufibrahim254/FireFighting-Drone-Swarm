package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneEventTest {
    private DroneEvent droneEvent;
    private final Drone drone = new Drone(1,100.00);

    @BeforeEach
    void setUp() {
        droneEvent = new DroneEvent(drone);
    }

    @Test
    void processEvent() {
        Event event = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        droneEvent.processEvent(event, 10, drone);
        assertEquals(90.0, drone.getAgentCapacity(), "Agent capacity should decrease by 50L");
        assertEquals(DroneState.IDLE, drone.getState(), "DroneState should return to IDLE after processing is complete");
    }
    @Test
    void refill() {
        droneEvent.processEvent(new Event("20:00", 1, EventType.FIRE_DETECTED, "High"), 10, drone);
        droneEvent.refill();
        assertEquals(100.0, drone.getAgentCapacity(), "Drone should be refilled to max capacity");
        assertEquals(DroneState.REFILLING, drone.getState(), "Drone state should be 'REFILLING' during refill");
    }

}