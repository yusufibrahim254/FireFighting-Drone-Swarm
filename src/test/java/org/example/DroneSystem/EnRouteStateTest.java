package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class EnRouteStateTest {
    private Drone drone;
    private EnRouteState enRouteState;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        enRouteState = new EnRouteState();
        drone.setState(enRouteState);
    }

    @Test
    void dispatch() {
        enRouteState.dispatch(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    @Test
    void arrive() {
        enRouteState.arrive(drone);
        assertTrue(drone.getState() instanceof DroppingAgentState);
    }

    @Test
    void dropAgent() {
        assertEquals(0, enRouteState.dropAgent(drone));
    }

    @Test
    void refill() {
        enRouteState.refill(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
    }

    @Test
    void fault() {
        enRouteState.fault(drone);
        assertTrue(drone.getState() instanceof FaultedState);
    }

    @Test
    void reset() {
        enRouteState.reset(drone);
        assertTrue(drone.getState() instanceof IdleState);
    }

}