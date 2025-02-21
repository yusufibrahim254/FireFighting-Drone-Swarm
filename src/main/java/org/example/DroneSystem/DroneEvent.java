package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

/**
 * Handler of the drone's event such as releasing agent and refilling drone
 */
public class DroneEvent {

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
        drone.setState(new DroppingAgentState());
        double agentCapacity = drone.getAgentCapacity();
        if (agentCapacity > waterNeeded){
            drone.setAgentCapacity(agentCapacity - waterNeeded);
            System.out.println("Drone " + drone.getId() + " released water agent, " + waterNeeded + " litres released");
            drone.setState(new IdleState());
            return 0.0;
        }
        System.out.println("Drone " + drone.getId() + " released water agent, " + agentCapacity + " litres released");
        waterNeeded = waterNeeded - agentCapacity;
        drone.setAgentCapacity(0.0);
        drone.setState(new IdleState());
        return waterNeeded;
    }


    /**
     * Responds to a fire incident event by dropping the required amount of agent at a specific location.
     *
     * @param waterNeeded The amount of agent needed to successfully assist in combating the fire.
     * @return The remaining agent needed after the drone's contribution2
     */
    public double dropAgent(double waterNeeded) throws InterruptedException {
        // Transition to dropping agent state and handle water amount logic
        return drone.getState().dropAgent(drone, waterNeeded);
    }

    /**
     * Refills the drone's firefighting agent to the drone's maximum capacity, and changes the state of the drone to refilling.
     */
    public void refill() {
        System.out.println("Drone " + drone.getId() + " is refilling");
        drone.getState().refill(drone);
    }

}
