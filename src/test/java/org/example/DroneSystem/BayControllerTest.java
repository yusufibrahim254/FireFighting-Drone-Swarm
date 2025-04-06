package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BayController class.
 * Ensures the drone bay door mechanisms open and close as expected.
 */
class BayControllerTest {
    private BayController bayController;
    private Drone drone;
    private DroneSubsystem droneSubsystem;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1, 15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        bayController = drone.getBayController();
    }

    /**
     * Test that the bay doors open correctly.
     * The door should be closed by default and open after calling openBayDoors().
     */
    @Test
    void openBayDoors() throws InterruptedException {
        assertFalse(bayController.isBayDoorOpen(), "Bay Doors should not be open, initially");
        bayController.openBayDoors();
        assertTrue(bayController.isBayDoorOpen(), "Bay Doors should be open");
    }

    /**
     * Test that the bay doors close correctly.
     * After opening the doors, they should be closable using closeBayDoors().
     */
    @Test
    void closeBayDoors() throws InterruptedException {
        bayController.openBayDoors();
        assertTrue(bayController.isBayDoorOpen(), "Bay Doors should be open, initially");
        bayController.closeBayDoors();
        assertFalse(bayController.isBayDoorOpen(), "Bay doors should be closed");
    }
}
