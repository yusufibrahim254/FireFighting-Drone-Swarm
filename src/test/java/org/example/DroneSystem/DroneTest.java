package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import org.example.FireIncidentSubsystem.Event;

import static org.junit.jupiter.api.Assertions.*;

class DroneTest {
    private Drone drone;
    private DroneState droneState = new IdleState();
    private Event event;
    private DatagramSocket testSocket;
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        testSocket = new DatagramSocket(0);
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High");
    }

    @AfterEach
    void tearDown() {
        testSocket.close();
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
        assertEquals(15, drone.getAgentCapacity(), "getAgentCapacity should return the initial full capacity.");
    }

    @Test
    void getId() {
        assertEquals(1, drone.getId());
    }

    @Test
    void getBattery() {
        assertEquals(100, drone.getBatteryLevel());
    }

    @Test
    void setAgentCapacity() {
        drone.setAgentCapacity(10);
        assertEquals(10, drone.getAgentCapacity());
    }

    @Test
    void getMaxAgentCapacity() {
        assertEquals(15, drone.getMaxAgentCapacity());
    }

    @Test
    void getBayController() {
        assertNotNull(drone.getBayController());
    }

    @Test
    void setCurrentPosition() {
        int[] newPosition = {5, 5};
        drone.setCurrentPosition(newPosition);
        assertEquals(Arrays.toString(newPosition), Arrays.toString(drone.getCurrentPosition()));
    }

    @Test
    void getRemainingWaterNeeded() {
        assertEquals(0, drone.getRemainingWaterNeeded());
    }

    @Test
    void setRemainingWaterNeeded() {
        drone.setRemainingWaterNeeded(5);
        assertEquals(5, drone.getRemainingWaterNeeded());
    }

    @Test
    void setBatteryLevel() {
        drone.setBatteryLevel(50);
        assertEquals(50, drone.getBatteryLevel());
    }

    @Test
    void setCurrentEvent() {
        Event newEvent = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low");
        drone.setCurrentEvent(newEvent);
        assertEquals(newEvent, drone.getCurrentEvent());
        assertEquals(10, drone.getRemainingWaterNeeded());

        drone.setCurrentEvent(null);
        assertNull(drone.getCurrentEvent());
        assertEquals(0, drone.getRemainingWaterNeeded());
    }

    @Test
    void getCurrentEvent() {
        assertNull(drone.getCurrentEvent());
        drone.setCurrentEvent(event);
        assertEquals(event, drone.getCurrentEvent());
    }

    @Test
    void getIncidentPosition() {
        assertNull(drone.getIncidentPosition());
    }

    @Test
    void setIncidentPosition() {
        int[] position = {100, 100};
        drone.setIncidentPosition(position);
        assertArrayEquals(position, drone.getIncidentPosition());
    }

    @Test
    void getBatteryDepletionRate() {
        assertEquals(0.1, drone.getBatteryDepletionRate());
    }

    @Test
    void getCurrentPosition() {
        assertEquals(Arrays.toString(new int[]{0, 0}), Arrays.toString(drone.getCurrentPosition()));
        int[] newPosition = {100, 100};
        drone.setCurrentPosition(newPosition);
        assertEquals(Arrays.toString(newPosition), Arrays.toString(drone.getCurrentPosition()));
    }

    @Test
    void setTargetPosition() {
        int[] position = {100, 100};
        drone.setTargetPosition(position);
        assertEquals(position, drone.getTargetPosition());
    }

    @Test
    void moveTowardsTarget() {
        drone.setTargetPosition(new int[]{3, 4});
        drone.moveTowardsTarget();
        assertEquals(3, drone.getCurrentPosition()[0]);
        assertEquals(4, drone.getCurrentPosition()[1]);
        assertTrue(drone.getBatteryLevel() < 100);
    }

}