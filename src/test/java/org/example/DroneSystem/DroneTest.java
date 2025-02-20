package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {
    private Drone drone;
    private DroneState droneState = new IdleState();


    @BeforeEach
    void setUp() {
        drone = new Drone(1,100.00);
    }

    @Test
    void getState() {
        droneState = new EnRouteState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "getState should return the current state.");
    }

    @Test
    void setState() {
        droneState = new FaultedState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "setState should return the current state.");
    }
    @Test
    void getAgentCapacity() {
        assertEquals(100.0, drone.getAgentCapacity(), "getAgentCapacity should return the initial full capacity.");
    }

    @Test
    void getId() {
    }

    @Test
    void getBattery() {
    }

    @Test
    void setAgentCapacity() {
    }

    @Test
    void getMaxAgentCapacity() {
    }

    @Test
    void getBayController() {
    }

}