package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {
    private Drone drone;

    @BeforeEach
    void setUp() {
        drone = new Drone(1,100.00);
    }

    @Test
    void getState() {
        drone.setState(DroneState.EN_ROUTE);
        assertEquals(DroneState.EN_ROUTE, drone.getState(), "getState should return the current state.");
    }

    @Test
    void setState() {
        drone.setState(DroneState.FAULTED);
        assertEquals(DroneState.FAULTED, drone.getState(), "setState should return the current state.");
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