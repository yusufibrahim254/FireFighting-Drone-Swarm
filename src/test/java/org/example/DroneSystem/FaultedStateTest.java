package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the FaultedState class.
 */
class FaultedStateTest {
    private Drone drone;
    private FaultedState faultedState;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and sets the state to FaultedState.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        faultedState = new FaultedState();
        drone.setState(faultedState);
    }

    /**
     * Test the dispatch method.
     * Verifies that the drone remains in FaultedState after dispatch.
     */
    @Test
    void dispatch() {
        faultedState.dispatch(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the arrive method.
     * Verifies that the drone remains in FaultedState after arrival.
     */
    @Test
    void arrive() {
        faultedState.arrive(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the dropAgent method.
     * Verifies that the dropAgent method returns 0 when called in FaultedState.
     */
    @Test
    void dropAgent() {
        assertEquals(0, faultedState.dropAgent(drone));
    }

    /**
     * Test the refill method.
     * Verifies that the drone remains in FaultedState after refilling.
     */
    @Test
    void refill() {
        faultedState.refill(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the fault method.
     * Verifies that the drone remains in FaultedState after a fault is invoked again.
     */
    @Test
    void fault() {
        faultedState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the reset method.
     * Verifies that the drone transitions to IdleState after resetting.
     */
    @Test
    void reset() {
        faultedState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }
}
