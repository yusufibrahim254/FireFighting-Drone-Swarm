package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class DroppingAgentStateTest {
    private Drone drone;
    private DroppingAgentState droppingAgentState;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        Event event = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low");
        droppingAgentState = new DroppingAgentState();
        drone.setState(droppingAgentState);
        drone.setCurrentEvent(event);
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
        assertEquals(0, droppingAgentState.dropAgent(drone));
        assertTrue(drone.getState() instanceof ReturningState);
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