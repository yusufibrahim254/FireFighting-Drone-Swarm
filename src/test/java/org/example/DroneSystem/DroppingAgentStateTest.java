package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroppingAgentStateTest {
    private Drone drone;
    private DroppingAgentState droppingAgentState;

    @BeforeEach
    void setUp() {
        drone = new Drone(1, 30);
        droppingAgentState = new DroppingAgentState();
        drone.setState(droppingAgentState);
    }

    @Test
    void dispatch() {
        droppingAgentState.dispatch(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    @Test
    void arrive() {
        droppingAgentState.arrive(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    @Test
    void dropAgent() throws InterruptedException {
        double waterNeeded = 40.0;
        assertEquals(10.0, droppingAgentState.dropAgent(drone, waterNeeded));
        assertTrue(drone.getState() instanceof RefillingState);
    }

    @Test
    void refill() {
        droppingAgentState.refill(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    @Test
    void fault() {
        droppingAgentState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void reset() {
        droppingAgentState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

}