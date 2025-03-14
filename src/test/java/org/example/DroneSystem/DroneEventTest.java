package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class DroneEventTest {

    private Drone drone;
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(7000, 8000, "docs/sample_zone_file.csv"), 0.1);
        DroneEvent droneEvent = new DroneEvent(drone);

    }

//    @Test
//    void processEvent() throws InterruptedException {
//        Event event = new Event(1, "20:00", 1, EventType.FIRE_DETECTED, "High");
//        DroneState droneState = new DroppingAgentState();
//        drone.setState(droneState);
//        drone.setAgentCapacity(10);
//        drone.getState().dropAgent(drone);
//        droneState.reset(drone);
//        assertEquals(5, drone.getAgentCapacity(), "Agent capacity should decrease by 5L");
//        assertEquals("IdleState", drone.getState().getClass().getSimpleName(), "DroneState should return to IDLE after processing is complete");
//    }
//
//    @Test
//    void refill() throws InterruptedException {
//        Event event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High");
//        drone.setState(new DroppingAgentState());
//        drone.setAgentCapacity(10);
//        drone.getState().dropAgent(drone);
//        assertEquals(100.0, drone.getAgentCapacity(), "Drone should be refilled to max capacity");
//        assertEquals("RefillingState", drone.getState().getClass().getSimpleName(), "Drone state should be 'REFILLING' during refill");
//    }

}