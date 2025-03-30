package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class RefillingStateTest {
    private Drone drone;
    private RefillingState refillingState;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        Event event = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        refillingState = new RefillingState();
        drone.setState(refillingState);
        drone.setCurrentEvent(event);
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
        assertEquals(0, refillingState.dropAgent(drone));
    }

    @Test
    void refill() {
        refillingState.refill(drone);
        assertTrue(drone.getState() instanceof EnRouteState);
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