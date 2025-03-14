package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class BayControllerTest {
    private BayController bayController;
    private Drone drone;
    private DroneSubsystem droneSubsystem;

    @BeforeEach
    void setUp() throws SocketException {
        drone = new Drone(1,15, new DroneSubsystem(0, 0, "docs/sample_zone_file.csv"), 0.1);
        bayController = drone.getBayController();
    }

    @Test
    void openBayDoors()throws InterruptedException {
        assertFalse(bayController.isBayDoorOpen(), "Bay Doors should not be open, initially");
        bayController.openBayDoors();
        assertTrue(bayController.isBayDoorOpen(), "Bay Doors should be open");
    }

    @Test
    void closeBayDoors() throws InterruptedException {
        bayController.openBayDoors();
        assertTrue(bayController.isBayDoorOpen(), "Bay Doors should be open, initially");
        bayController.closeBayDoors();
        assertFalse(bayController.isBayDoorOpen(), "Bay doors should be closed");
    }

}