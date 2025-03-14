package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class FaultedStateTest {
    private Drone drone;
    private FaultedState faultedState;
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);

        faultedState = new FaultedState();
        drone.setState(faultedState);
    }

    @Test
    void dispatch() {
        faultedState.dispatch(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void arrive() {
        faultedState.arrive(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void dropAgent() {
        assertEquals(0, faultedState.dropAgent(drone));
    }

    @Test
    void refill() {
        faultedState.refill(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void fault() {
        faultedState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void reset() {
        faultedState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

}