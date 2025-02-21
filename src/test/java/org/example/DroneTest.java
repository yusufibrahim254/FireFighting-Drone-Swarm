package org.example;

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
    void processEvent() throws InterruptedException {
        Event event = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        droneState = new DroppingAgentState();
        drone.setState(droneState);
        drone.setAgentCapacity(10);
        drone.dropAgent(5);
        droneState.reset(drone);
        assertEquals(5, drone.getAgentCapacity(), "Agent capacity should decrease by 5L");
        assertEquals("IdleState", drone.getState().getClass().getSimpleName(), "DroneState should return to IDLE after processing is complete");
    }

    @Test
    void openBayDoors()throws InterruptedException {
        assertFalse(drone.isBayDoorOpen(), "Bay Doors should not be open, initially");
        drone.openBayDoors();
        assertTrue(drone.isBayDoorOpen(), "Bay Doors should be open");
    }

    @Test
    void closeBayDoors() throws InterruptedException {
        drone.openBayDoors();
        assertTrue(drone.isBayDoorOpen(), "Bay Doors should be open, initially");
        drone.closeBayDoors();
        assertFalse(drone.isBayDoorOpen(), "Bay doors should be closed");
    }

    @Test
    void refill() throws InterruptedException {
        Event event2 = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        drone.setState(new DroppingAgentState());
        drone.setAgentCapacity(10);
        drone.dropAgent(20);
        assertEquals(100.0, drone.getAgentCapacity(), "Drone should be refilled to max capacity");
        assertEquals("RefillingState", drone.getState().getClass().getSimpleName(), "Drone state should be 'REFILLING' during refill");
    }

    @Test
    void getState() {
        droneState = new EnRouteState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "getState should return the current state.");
    }

    @Test
    void getAgentCapacity() {
        assertEquals(100.0, drone.getAgentCapacity(), "getAgentCapacity should return the initial full capacity.");
    }

    @Test
    void setState() {
        droneState = new FaultedState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "setState should return the current state.");
    }
}