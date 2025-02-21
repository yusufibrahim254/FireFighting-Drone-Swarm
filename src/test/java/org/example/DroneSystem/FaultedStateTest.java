package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaultedStateTest {
    private Drone drone;
    private FaultedState faultedState;
    @BeforeEach
    void setUp() {
        drone = new Drone(1, 30);
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
        double waterNeeded = 10.0;
        assertEquals(waterNeeded, faultedState.dropAgent(drone, waterNeeded));
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