package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdleStateTest {
    private Drone drone;
    private IdleState idleState;

    @BeforeEach
    void setUp() {
        drone = new Drone(1, 30);
        idleState = new IdleState();
        drone.setState(idleState);
    }

    @Test
    void dispatch() {
        idleState.dispatch(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    @Test
    void arrive() {
        idleState.arrive(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

    @Test
    void dropAgent() {
        double waterNeeded = 10.0;
        assertEquals(waterNeeded, idleState.dropAgent(drone, waterNeeded));
    }

    @Test
    void refill() {
        idleState.refill(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

    @Test
    void fault() {
        idleState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void reset() {
        idleState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

}