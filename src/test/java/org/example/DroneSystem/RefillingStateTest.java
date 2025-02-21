package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefillingStateTest {
    private Drone drone;
    private RefillingState refillingState;

    @BeforeEach
    void setUp() {
        drone = new Drone(1, 30);
        refillingState = new RefillingState();
        drone.setState(refillingState);
    }

    @Test
    void dispatch() {
        refillingState.dispatch(drone);
        assertTrue(drone.getState() instanceof RefillingState);
    }

    @Test
    void arrive() {
        refillingState.arrive(drone);
        assertTrue(drone.getState() instanceof RefillingState);
    }

    @Test
    void dropAgent() {
        double waterNeeded = 10.0;
        assertEquals(waterNeeded, refillingState.dropAgent(drone, waterNeeded));
    }

    @Test
    void refill() {
        refillingState.refill(drone);
        assertTrue(drone.getState() instanceof RefillingState);
    }

    @Test
    void fault() {
        refillingState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void reset() {
        refillingState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

}