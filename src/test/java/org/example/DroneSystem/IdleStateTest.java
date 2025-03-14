package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class IdleStateTest {
    private Drone drone;
    private IdleState idleState;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
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
        assertEquals(0, idleState.dropAgent(drone));
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