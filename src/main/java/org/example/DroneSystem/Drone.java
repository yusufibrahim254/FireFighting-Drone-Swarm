/**
 * Represents a drone used in a firefighting drone swarm system.
 * The drone can process events, release a special firefighting agent, open and close bay doors, and refill the agent in its supply.
 * The drone subsystem operates as an individual process and communicates with the Scheduler for information about the fire.
 */
package org.example.DroneSystem;

/**
 * The Drone class models a firefighting drone that assists in fighting fires.
 * It is responsible for interacting with the Scheduler to find and respond to fires, manage firefighting agent, and responding to fires.
 */
public class Drone{

    /**
     * Unique identifier that identifies each drone.
     */
    private int id;

    /**
     * Current battery level of the drone, represented as a percentage (0-100)
     */
    private int battery;

    /**
     * Current amount of agent available in the drone (Liters)
     */
    private double agentCapacity;

    /**
     * Maximum amount of agent that the drone can carry (Liters)
     */
    private final double maxAgentCapacity;


    /**
     * The current state of the Drone. (enum DroneState)
     */
    private DroneState state;

    private final BayController bayController;


    /**
     * Constructs a Drone with a specified ID and initial agent capacity.
     *
     * @param id Unique identifier of each drone
     * @param initialCapacity The initial amount of agent that the drone can carry.
     */
    public Drone(int id, double initialCapacity) {
        this.id = id;
        this.battery = 100;
        this.agentCapacity = initialCapacity;
        this.maxAgentCapacity = initialCapacity;
        this.bayController = new BayController();
        this.state = new IdleState();
    }

    /**
     * Gets the current state of the drone.
     *
     * @return The current state of the drone
     */
    public DroneState getState() {
        return state;
    }

    /**
     * Sets the state of the drone
     *
     * @param newState The new state of the drone.
     */
    public void setState(DroneState newState) {
        System.out.println("*Transitioning from " + state.getClass().getSimpleName() + " to " + newState.getClass().getSimpleName() + "*");
        this.state = newState;
    }

    /**
     * Gets the current firefighting agent capacity of the drone.
     *
     * @return The remaining firefighting agent capacity in Liters.
     */
    public double getAgentCapacity() {
        return agentCapacity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getBattery() {
        return battery;
    }

    /**
     * Helper methods to manage agent capacity and state
     *
     * @param agentCapacity The amount of agent the drone can take
     */
    public void setAgentCapacity(double agentCapacity) {
        this.agentCapacity = agentCapacity;
    }

    /**
     * Gets the maximum amount of firefighting agent the drone can carry.
     *
     * @return The maximum firefighting agent capacity in Liters.
     */
    public double getMaxAgentCapacity() {
        return maxAgentCapacity;
    }


    public BayController getBayController() {
        return bayController;
    }
}
