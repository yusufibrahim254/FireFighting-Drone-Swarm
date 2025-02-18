/**
 * Represents the Drone Subsystem responsible for managing and coordinating multiple drones in the firefighting drone swarm system.
 * The DroneSubsystem interacts with the Scheduler to assign drones to fire incidents and handle drone operations.
 */

package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

import java.util.List;


/**
 * The DroneSubsystem class manages a fleet of drones, by assigning them to fire incidents and coordinating their operations,
 * such as firefighting agent release and refilling, to ensure optimal firefighting performance.
 */
public class DroneSubsystem {
    private FleetManager fleetManager = new FleetManager();
    private final List<Drone> drones = fleetManager.getDrones();

    /**
     * Constructs a DroneSubsystem with a given scheduler and initializes drones.
     *
     */
    public DroneSubsystem() {

    }

    /**
     * Assigns an available drone to a fire incident event.
     *
     * @param event the fire incident event to be handled.
     */
    public void assignDroneToEvent(Event event) {
        double waterNeeded = event.getSeverityWaterAmount();
        boolean assigned = false;


        for (Drone drone : drones) {
            DroneEvent droneEvent = new DroneEvent(drone);
            if (drone.getState() == DroneState.IDLE) {
                assigned = true;
                System.out.println("Assigning drone to event: ");
                drone.setState(DroneState.EN_ROUTE);
                System.out.println("Drone state set to EN_ROUTE");


                try {
                    drone.getBayController().openBayDoors();
                    waterNeeded = droneEvent.processEvent(event, waterNeeded, drone);
                    System.out.println("Water needed to finish off fire " + waterNeeded);
                    drone.getBayController().closeBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error with bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                if (drone.getAgentCapacity() == 0) {
                    System.out.println("Drone needs to refill.");
                    droneEvent.refill();
                }

                if (waterNeeded <= 0) {
                    // Fire extinguished stop looking for more drones
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


