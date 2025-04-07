package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the RefillingState class.
 */
class RefillingStateTest {
    private Drone drone;
    private RefillingState refillingState;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and sets the state to RefillingState.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        Event event = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        refillingState = new RefillingState();
        drone.setState(refillingState);
        drone.setCurrentEvent(event);
    }

    /**
     * Test the dispatch method.
     * Verifies that the drone remains in RefillingState when dispatched.
     */
    @Test
    void dispatch() {
        refillingState.dispatch(drone);
        assertTrue(drone.getState() instanceof RefillingState);
    }

    /**
     * Test the arrive method.
     * Verifies that the drone remains in RefillingState after arrival.
     */
    @Test
    void arrive() {
        refillingState.arrive(drone);
        assertTrue(drone.getState() instanceof RefillingState);
    }

    /**
     * Test the dropAgent method.
     * Verifies that the dropAgent method returns 0 when called in RefillingState.
     */
    @Test
    void dropAgent() {
        assertEquals(0, refillingState.dropAgent(drone));
    }

    /**
     * Test the refill method.
     * Verifies that the drone transitions to EnRouteState after refilling.
     */
    @Test
    void refill() {
        refillingState.refill(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    /**
     * Test the fault method.
     * Verifies that the drone transitions to FaultedState after a fault is invoked.
     */
    @Test
    void fault() {
        refillingState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the reset method.
     * Verifies that the drone transitions to IdleState after resetting.
     */
    @Test
    void reset() {
        refillingState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }
}
