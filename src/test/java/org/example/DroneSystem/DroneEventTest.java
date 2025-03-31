package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class DroneEventTest {

    private Drone drone;
    private DroneEvent droneEvent;
    private Event event;
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        droneEvent = new DroneEvent(drone);
        event = new Event(1, "12:12:12", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        drone.setCurrentEvent(event);
    }

    @Test
    void processEvent_SufficientAgent() {
        double remainingWater = droneEvent.processEvent(event, 10.0, drone);
        assertEquals(5.0, drone.getAgentCapacity());
        assertEquals(0.0, remainingWater);
        assertTrue(drone.getState() instanceof IdleState);
    }

    @Test
    void processEvent_InsufficientAgent() {
        double remainingWater = droneEvent.processEvent(event, 20.0, drone);
        assertEquals(0.0, drone.getAgentCapacity());
        assertEquals(5.0, remainingWater);
        assertTrue(drone.getState() instanceof IdleState);
    }

    @Test
    void dropAgent() throws InterruptedException {
        drone.setState(new DroppingAgentState());
        double result = droneEvent.dropAgent(5.0);
        assertEquals(0.0, result);
    }

    @Test
    void refill() {
        drone.setState(new RefillingState());
        drone.setAgentCapacity(5.0);
        droneEvent.refill();
        assertEquals(15.0, drone.getAgentCapacity());
    }
}