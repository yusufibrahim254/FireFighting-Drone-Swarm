package org.example.DroneSystem;

/**
 * Controller of the bay doors on drone
 */
public class BayController {
    /**
     * Flag that indicates if the bay doors are currently open (true) or closed (false).
     */
    private boolean bayDoorOpen;

    public BayController() {
        this.bayDoorOpen = false;
    }

    /**
     * Opens the bay doors after a delay (to simulate a real drone) to allow the agent to fall onto the fire.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public void openBayDoors() throws InterruptedException {
        System.out.println("Bay doors opened.");
        Thread.sleep(3000);
        bayDoorOpen = true;
    }

    /**
     * Closes the bay doors after a delay (to simulate a real drone) the drone has completed releasing agent onto the fire.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public void closeBayDoors() throws InterruptedException {
        System.out.println("Bay doors closing.");
        Thread.sleep(3000);
        bayDoorOpen = false;
        System.out.println("Bay doors closed.");
    }


    /**
     * Checks if the bay doors of the drone are open.
     *
     * @return True if the bay doors are open, false if they are closed.
     */
    public boolean isBayDoorOpen() {
        return bayDoorOpen;
    }
}
