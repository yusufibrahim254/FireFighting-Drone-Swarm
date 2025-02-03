/**
 * Represents the Drone Subsystem responsible for managing and coordinating multiple drones in the firefighting drone swarm system.
 * The DroneSubsystem interacts with the Scheduler to assign drones to fire incidents and handle drone operations.
 */

package org.example;

import org.example.FireIncidentSubsystem.Event;
import java.util.ArrayList;
import java.util.List;


/**
 * The DroneSubsystem class manages a fleet of drones, by assigning them to fire incidents and coordinating their operations,
 * such as firefighting agent release and refilling, to ensure optimal firefighting performance.
 */
public class DroneSubsystem implements Runnable {

    /**
     * List of drones managed by this subsystem.
     */
    private final List<Drone> drones;

    /**
     * The Scheduler instance responsible for coordinating
     */
    private final Scheduler scheduler;

    /**
     * Constructs a DroneSubsystem with a given scheduler and initializes drones.
     * @param scheduler the scheduler instance used to retrieve fire incidents.
     */
    public DroneSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.drones = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            drones.add(new Drone(i,15));
        }
    }

    /**
     * Assigns an available drone to a fire incident event.
     * @param event the fire incident event to be handled.
     */
    public void assignDroneToEvent(Event event) {
        double waterNeeded = event.getSeverityWaterAmount();
        for (Drone drone : drones) {
            if (drone.getState() == DroneState.IDLE) {
                System.out.println("Assigning drone to event: ");
                drone.setState(DroneState.EN_ROUTE);
                System.out.println("Drone state set to EN_ROUTE");

                try {
                    drone.openBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error opening bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                waterNeeded = drone.processEvent(event, waterNeeded);
                System.out.println("Water needed to finish off fire " + waterNeeded);

                try {
                    drone.closeBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error closing bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                if (drone.getAgentCapacity() == 0) {
                    System.out.println("Drone needs to refill.");
                    drone.refill();
                }

                if (waterNeeded <= 0){
                    return;
                }
            }

            System.out.println("no available drones");
        }
    }

    /**
     * Runs the DroneSubsystem, processing events from the scheduler and assigning drones.
     */
    @Override
    public void run() {
        //guarantee it starts after fireincident thread, and since scheduler waits it will ensure fireincident->scheduler->drone
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (scheduler) {
            while (!scheduler.isEmpty()) {
                Event event = scheduler.getEvent();
                if (event != null) {
                    System.out.println("DroneSubsystem: Processing event - " + event);
                    assignDroneToEvent(event);
                    System.out.println();
                }
            }
            System.out.println("DroneSubsystem: Sending Confirmation back to Scheduler");
            scheduler.notifyAll();
        }
    }
}

