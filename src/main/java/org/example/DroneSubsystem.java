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
public class DroneSubsystem {

    /**
     * List of drones managed by this subsystem.
     */
    private final List<Drone> drones;



    /**
     * Constructs a DroneSubsystem with a given scheduler and initializes drones.
     *
     */
    public DroneSubsystem() {
        this.drones = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            drones.add(new Drone(i, 15));
        }
    }

    /**
     * Assigns an available drone to a fire incident event.
     *
     * @param event the fire incident event to be handled.
     */
    public void assignDroneToEvent(Event event) throws InterruptedException {
        double waterNeeded = event.getSeverityWaterAmount();
        boolean assigned = false;

        for (Drone drone : drones) {
            if (drone.getState() instanceof IdleState) {
                assigned = true;
                System.out.println("Assigning drone to event: ");

                // Dispatch drone to the fire zone
                drone.getState().dispatch(drone);

                // Transition drone to Dropping Agent State as soon as it arrives
                drone.getState().arrive(drone);

                try {
                    drone.openBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error opening bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                waterNeeded = drone.dropAgent(waterNeeded);

                if (waterNeeded <= 0) {
                    // Fire extinguished, stop looking for more drones
                    break;
                }
            }
        }

        if (!assigned) {
            // If we never assigned a drone
            System.out.println("no available drones");
        }
    }
}


