package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the EnRouteState class.
 */
class EnRouteStateTest {
    private Drone drone;
    private EnRouteState enRouteState;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and sets the state to EnRouteState.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        enRouteState = new EnRouteState();
        drone.setState(enRouteState);
    }

    /**
     * Test the dispatch method.
     * Verifies that the drone remains in EnRouteState after dispatch.
     */
    @Test
    void dispatch() {
        enRouteState.dispatch(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    /**
     * Test the arrive method.
     * Verifies that the drone transitions to DroppingAgentState after arrival.
     */
    @Test
    void arrive() {
        enRouteState.arrive(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    /**
     * Test the dropAgent method.
     * Verifies that the dropAgent method returns 0 when called.
     */
    @Test
    void dropAgent() {
        assertEquals(0, enRouteState.dropAgent(drone));
    }

    /**
     * Test the refill method.
     * Verifies that the drone remains in EnRouteState after refilling.
     */
    @Test
    void refill() {
        enRouteState.refill(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    /**
     * Test the fault method.
     * Verifies that the drone transitions to FaultedState after a fault.
     */
    @Test
    void fault() {
        enRouteState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the reset method.
     * Verifies that the drone transitions to IdleState after resetting.
     */
    @Test
    void reset() {
        enRouteState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }
}
