package org.example.DroneSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BayControllerTest {
    private BayController bayController;
    private Drone drone;
    @BeforeEach
    void setUp() {
        drone = new Drone(1,100.00);
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