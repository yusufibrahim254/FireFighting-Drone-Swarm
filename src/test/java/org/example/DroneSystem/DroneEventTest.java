package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DroneEvent class.
 * Tests ensure correct agent (water) handling, state transitions,
 * and drone response to fire events.
 */
class DroneEventTest {

    private Drone drone;
    private DroneEvent droneEvent;
    private Event event;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        droneEvent = new DroneEvent(drone);
        event = new Event(1, "12:12:12", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        drone.setCurrentEvent(event);
    }

    /**
     * Tests the drone event processing when there is enough agent (e.g., water)
     * to fully extinguish the fire. Verifies that agent capacity is reduced correctly,
     * and the drone transitions to IdleState.
     */
    @Test
    void processEvent_SufficientAgent() {
        double remainingWater = droneEvent.processEvent(event, 10.0, drone);
        assertEquals(5.0, drone.getAgentCapacity());
        assertEquals(0.0, remainingWater);
        assertTrue(drone.getState() instanceof IdleState);
    }

    /**
     * Tests event processing when the drone does not have enough agent
     * to extinguish the fire. Checks if agent capacity drops to 0
     * and remaining water is calculated correctly.
     */
    @Test
    void processEvent_InsufficientAgent() {
        double remainingWater = droneEvent.processEvent(event, 20.0, drone);
        assertEquals(0.0, drone.getAgentCapacity());
        assertEquals(5.0, remainingWater);
        assertTrue(drone.getState() instanceof IdleState);
    }

    /**
     * Tests the drone's behavior while in DroppingAgentState.
     * Ensures dropAgent consumes all agent when requested amount is available.
     */
    @Test
    void dropAgent() throws InterruptedException {
        drone.setState(new DroppingAgentState());
        double result = droneEvent.dropAgent(5.0);
        assertEquals(0.0, result);
    }

    /**
     * Tests the refill behavior when the drone is in RefillingState.
     * Verifies agent capacity is restored to full.
     */
    @Test
    void refill() {
        drone.setState(new RefillingState());
        drone.setAgentCapacity(5.0);
        droneEvent.refill();
        assertEquals(15.0, drone.getAgentCapacity());
    }
}
