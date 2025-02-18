package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

public class DroneEvent {
    /**
     * The current state of the Drone. (enum DroneState)
     */

    private Drone drone;

    public DroneEvent(Drone drone) {
        this.drone = drone;
    }

    /**
     * Responds to a fire incident event by dropping the required amount of agent at a specific location.
     *
     * @param event The fire incident event
     * @param waterNeeded The amount of agent needed to successfully assist in combating the fire.
     * @return The remaining agent needed after the drone's contribution2
     */
    public double processEvent(Event event, double waterNeeded, Drone drone) {
        drone.setState(DroneState.DROPPING_AGENT);
        double agentCapacity = drone.getAgentCapacity();
        if (agentCapacity > waterNeeded){
            drone.setAgentCapacity(agentCapacity - waterNeeded);
            System.out.println("Drone released water agent, " + waterNeeded + " litres released");
            drone.setState(DroneState.IDLE);
            return 0.0;
        }
        System.out.println("Drone released water agent, " + agentCapacity + " litres released");
        waterNeeded = waterNeeded - agentCapacity;
        drone.setAgentCapacity(0.0);
        drone.setState(DroneState.IDLE);
        return waterNeeded;
    }

    /**
     * Refills the drone's firefighting agent to the drone's maximum capacity, and changes the state of the drone to refilling.
     */
    public void refill() {
        drone.setState(DroneState.REFILLING);
        System.out.println("Drone is refilling");
        drone.setAgentCapacity(drone.getMaxAgentCapacity());
    }

}
