package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DroneEventTest {
    private DroneEvent droneEvent;
    private DroneState droneState = new IdleState();


    private final Drone drone = new Drone(1,100.00);

    @BeforeEach
    void setUp() {
        droneEvent = new DroneEvent(drone);
    }

    @Test
    void processEvent() throws InterruptedException {
        Event event = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        droneState = new DroppingAgentState();
        drone.setState(droneState);
        drone.setAgentCapacity(10);
        drone.getState().dropAgent(drone,5);
        droneState.reset(drone);
        assertEquals(5, drone.getAgentCapacity(), "Agent capacity should decrease by 5L");
        assertEquals("IdleState", drone.getState().getClass().getSimpleName(), "DroneState should return to IDLE after processing is complete");
    }

    @Test
    void refill() throws InterruptedException {
        Event event2 = new Event("20:00", 1, EventType.FIRE_DETECTED, "High");
        drone.setState(new DroppingAgentState());
        drone.setAgentCapacity(10);
        drone.getState().dropAgent(drone,20);
        assertEquals(100.0, drone.getAgentCapacity(), "Drone should be refilled to max capacity");
        assertEquals("RefillingState", drone.getState().getClass().getSimpleName(), "Drone state should be 'REFILLING' during refill");
    }

}