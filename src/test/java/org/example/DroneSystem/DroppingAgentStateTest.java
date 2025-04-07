package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the DroppingAgentState class.
 */
class DroppingAgentStateTest {
    private Drone drone;
    private DroppingAgentState droppingAgentState;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and sets the state to DroppingAgentState.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        Event event = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        droppingAgentState = new DroppingAgentState(drone.getDroneSubsystem());
        drone.setState(droppingAgentState);
        drone.setCurrentEvent(event);
    }

    /**
     * Test the dispatch method.
     * Verifies that the drone remains in the DroppingAgentState after dispatch.
     */
    @Test
    void dispatch() {
        droppingAgentState.dispatch(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    /**
     * Test the arrive method.
     * Verifies that the drone remains in the DroppingAgentState after arrival.
     */
    @Test
    void arrive() {
        droppingAgentState.arrive(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    /**
     * Test the dropAgent method.
     * Verifies that the drone transitions to ReturningState after dropping the agent.
     *
     * @throws InterruptedException if the thread is interrupted during the test
     */
    @Test
    void dropAgent() throws InterruptedException {
        assertEquals(0, droppingAgentState.dropAgent(drone), "dropAgent should return 0 indicating the agent was successfully dropped.");
        assertTrue(drone.getState() instanceof ReturningState);
    }

    /**
     * Test the refill method.
     * Verifies that the drone remains in DroppingAgentState after refilling.
     */
    @Test
    void refill() {
        droppingAgentState.refill(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    /**
     * Test the fault method.
     * Verifies that the drone transitions to FaultedState after a fault.
     */
    @Test
    void fault() {
        droppingAgentState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the reset method.
     * Verifies that the drone transitions to IdleState after resetting.
     */
    @Test
    void reset() {
        droppingAgentState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }
}
