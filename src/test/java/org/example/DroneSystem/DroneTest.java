package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.Arrays;

import org.example.FireIncidentSubsystem.Event;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Drone class.
 */
class DroneTest {
    private Drone drone;
    private DroneState droneState = new IdleState();
    private Event event, droneStuckEvent, nozzleJammedEvent;
    private DatagramSocket testSocket;

    /**
     * Set up the test environment before each test.
     * Initializes a Drone instance and necessary events.
     *
     * @throws SocketException if a socket error occurs
     */
    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        testSocket = new DatagramSocket(0);
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT");
        droneStuckEvent = new Event(2, "09:09:09", 3, EventType.DRONE_REQUEST, "Low", "DRONE_STUCK");
        nozzleJammedEvent = new Event(3, "10:10:10", 4, EventType.DRONE_REQUEST, "Low", "NOZZLE_JAMMED");
    }

    /**
     * Clean up resources after each test.
     */
    @AfterEach
    void tearDown() {
        testSocket.close();
    }

    /**
     * Test the getState method.
     * Verifies that the current state of the drone is returned correctly.
     */
    @Test
    void getState() {
        droneState = new EnRouteState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "getState should return the current state.");
    }

    /**
     * Test the setState method.
     * Verifies that the state of the drone is set correctly.
     */
    @Test
    void setState() {
        droneState = new FaultedState();
        drone.setState(droneState);
        assertEquals(droneState, drone.getState(), "setState should return the current state.");
    }

    /**
     * Test the getAgentCapacity method.
     * Verifies that the initial agent capacity is returned correctly.
     */
    @Test
    void getAgentCapacity() {
        assertEquals(15, drone.getAgentCapacity(), "getAgentCapacity should return the initial full capacity.");
    }

    /**
     * Test the getId method.
     * Verifies that the drone ID is returned correctly.
     */
    @Test
    void getId() {
        assertEquals(1, drone.getId());
    }

    /**
     * Test the getBattery method.
     * Verifies that the initial battery level is returned correctly.
     */
    @Test
    void getBattery() {
        assertEquals(100, drone.getBatteryLevel());
    }

    /**
     * Test the setAgentCapacity method.
     * Verifies that the agent capacity is set correctly.
     */
    @Test
    void setAgentCapacity() {
        drone.setAgentCapacity(10);
        assertEquals(10, drone.getAgentCapacity());
    }

    /**
     * Test the getMaxAgentCapacity method.
     * Verifies that the maximum agent capacity is returned correctly.
     */
    @Test
    void getMaxAgentCapacity() {
        assertEquals(15, drone.getMaxAgentCapacity());
    }

    /**
     * Test the getBayController method.
     * Verifies that the bay controller is not null.
     */
    @Test
    void getBayController() {
        assertNotNull(drone.getBayController());
    }

    /**
     * Test the setCurrentPosition method.
     * Verifies that the current position is set and retrieved correctly.
     */
    @Test
    void setCurrentPosition() {
        int[] newPosition = {5, 5};
        drone.setCurrentPosition(newPosition);
        assertEquals(Arrays.toString(newPosition), Arrays.toString(drone.getCurrentPosition()));
    }

    /**
     * Test the getRemainingWaterNeeded method.
     * Verifies that the remaining water needed is returned correctly.
     */
    @Test
    void getRemainingWaterNeeded() {
        assertEquals(0, drone.getRemainingWaterNeeded());
    }

    /**
     * Test the setRemainingWaterNeeded method.
     * Verifies that the remaining water needed is set correctly.
     */
    @Test
    void setRemainingWaterNeeded() {
        drone.setRemainingWaterNeeded(5);
        assertEquals(5, drone.getRemainingWaterNeeded());
    }

    /**
     * Test the setBatteryLevel method.
     * Verifies that the battery level is set correctly.
     */
    @Test
    void setBatteryLevel() {
        drone.setBatteryLevel(50);
        assertEquals(50, drone.getBatteryLevel());
    }

    /**
     * Test the setCurrentEvent method.
     * Verifies that the current event is set correctly and updates the remaining water needed.
     */
    @Test
    void setCurrentEvent() {
        Event newEvent = new Event(8, "08:07:06", 2, EventType.FIRE_DETECTED, "Low", "NO_FAULT");
        drone.setCurrentEvent(newEvent);
        assertEquals(newEvent, drone.getCurrentEvent());
        assertEquals(10, drone.getRemainingWaterNeeded());

        drone.setCurrentEvent(null);
        assertNull(drone.getCurrentEvent());
        assertEquals(0, drone.getRemainingWaterNeeded());
    }

    /**
     * Test the getCurrentEvent method.
     * Verifies that the current event is retrieved correctly.
     */
    @Test
    void getCurrentEvent() {
        assertNull(drone.getCurrentEvent());
        drone.setCurrentEvent(event);
        assertEquals(event, drone.getCurrentEvent());
    }

    /**
     * Test the getIncidentPosition method.
     * Verifies that the incident position is retrieved correctly.
     */
    @Test
    void getIncidentPosition() {
        assertNull(drone.getIncidentPosition());
    }

    /**
     * Test the setIncidentPosition method.
     * Verifies that the incident position is set correctly.
     */
    @Test
    void setIncidentPosition() {
        int[] position = {100, 100};
        drone.setIncidentPosition(position);
        assertArrayEquals(position, drone.getIncidentPosition());
    }

    /**
     * Test the getBatteryDepletionRate method.
     * Verifies that the battery depletion rate is retrieved correctly.
     */
    @Test
    void getBatteryDepletionRate() {
        assertEquals(0.1, drone.getBatteryDepletionRate());
    }

    /**
     * Test the getCurrentPosition method.
     * Verifies that the current position is retrieved correctly.
     */
    @Test
    void getCurrentPosition() {
        assertEquals(Arrays.toString(new int[]{0, 0}), Arrays.toString(drone.getCurrentPosition()));
        int[] newPosition = {100, 100};
        drone.setCurrentPosition(newPosition);
        assertEquals(Arrays.toString(newPosition), Arrays.toString(drone.getCurrentPosition()));
    }

    /**
     * Test the setTargetPosition method.
     * Verifies that the target position is set correctly.
     */
    @Test
    void setTargetPosition() {
        int[] position = {100, 100};
        drone.setTargetPosition(position);
        assertEquals(position, drone.getTargetPosition());
    }

    /**
     * Test the moveTowardsTarget method.
     * Verifies that the drone moves towards the target and battery is consumed.
     */
    @Test
    void moveTowardsTarget() {
        drone.setTargetPosition(new int[]{3, 4});
        drone.moveTowardsTarget();
        assertEquals(3, drone.getCurrentPosition()[0]);
        assertEquals(4, drone.getCurrentPosition()[1]);
        assertTrue(drone.getBatteryLevel() < 100);
    }

    /**
     * Test the drone stuck fault condition.
     * Verifies that the drone returns 'DRONE_STUCK' when it is stuck.
     */
    @Test
    void testDroneStuck() {
        drone.setCurrentEvent(droneStuckEvent);
        drone.setTargetPosition(new int[]{100, 100});
        String result = drone.moveTowardsTarget();
        assertEquals("DRONE_STUCK", result, "Drone should return 'DRONE_STUCK' when stuck");
    }

    /**
     * Test the nozzle jammed fault condition.
     * Verifies that the drone returns 'NOZZLE_JAMMED' when the nozzle is jammed.
     */
    @Test
    void testNozzleJammed() {
        drone.setCurrentEvent(nozzleJammedEvent);
        drone.setTargetPosition(new int[]{50, 50});
        String result = drone.moveTowardsTarget();
        assertEquals("NOZZLE_JAMMED", result, "Drone should return 'NOZZLE_JAMMED' when the nozzle is jammed");
    }
}
