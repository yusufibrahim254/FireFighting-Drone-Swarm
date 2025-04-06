package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the IdleState class.
 */
class IdleStateTest {
    private Drone drone;
    private IdleState idleState;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and sets the state to IdleState.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        idleState = new IdleState();
        drone.setState(idleState);
    }

    /**
     * Test the dispatch method.
     * Verifies that the drone transitions to EnRouteState when dispatched.
     */
    @Test
    void dispatch() {
        idleState.dispatch(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    /**
     * Test the arrive method.
     * Verifies that the drone remains in IdleState after arrival.
     */
    @Test
    void arrive() {
        idleState.arrive(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

    /**
     * Test the dropAgent method.
     * Verifies that the dropAgent method returns 0 when called in IdleState.
     */
    @Test
    void dropAgent() {
        assertEquals(0, idleState.dropAgent(drone));
    }

    /**
     * Test the refill method.
     * Verifies that the drone remains in IdleState after refilling.
     */
    @Test
    void refill() {
        idleState.refill(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

    /**
     * Test the fault method.
     * Verifies that the drone transitions to FaultedState after a fault is invoked.
     */
    @Test
    void fault() {
        idleState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    /**
     * Test the reset method.
     * Verifies that the drone remains in IdleState after resetting.
     */
    @Test
    void reset() {
        idleState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }
}
