/**
 * Represents a drone used in a firefighting drone swarm system.
 * The drone can process events, release a special firefighting agent, open and close bay doors, and refill the agent in its supply.
 * The drone subsystem operates as an individual process and communicates with the Scheduler for information about the fire.
 */
package org.example;

import org.example.FireIncidentSubsystem.Event;

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
     * Flag that indicates if the bay doors are currently open (true) or closed (false).
     */
    private boolean bayDoorOpen;

    /**
     * The current state of the Drone. (interface DroneState)
     */
    private DroneState state;


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
        this.bayDoorOpen = false;
        this.state = new IdleState();
    }

    /**
     * Responds to a fire incident event by dropping the required amount of agent at a specific location.
     *
     * @param waterNeeded The amount of agent needed to successfully assist in combating the fire.
     * @return The remaining agent needed after the drone's contribution2
     */
    public double dropAgent(double waterNeeded){
        // Transition to dropping agent state and handle water amount logic
        return state.dropAgent(this, waterNeeded);
    }

    /**
     * Opens the bay doors after a delay (to simulate a real drone) to allow the agent to fall onto the fire.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public void openBayDoors() throws InterruptedException {
        Thread.sleep(3000);
        bayDoorOpen = true;
        System.out.println("Bay doors opened.");
    }

    /**
     * Closes the bay doors after a delay (to simulate a real drone) the drone has completed releasing agent onto the fire.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    public void closeBayDoors() throws InterruptedException {
        Thread.sleep(3000);
        bayDoorOpen = false;
        System.out.println("Bay doors closed.");
    }

    /**
     * Refills the drone's firefighting agent to the drone's maximum capacity, and changes the state of the drone to refilling.
     */
    public void refill() {
        agentCapacity = maxAgentCapacity;
        state.refill(this);
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
     * Gets the current firefighting agent capacity of the drone.
     *
     * @return The remaining firefighting agent capacity in Liters.
     */
    public double getAgentCapacity() {
        return agentCapacity;
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
     * Checks if the bay doors of the drone are open.
     *
     * @return True if the bay doors are open, false if they are closed.
     */
    public boolean isBayDoorOpen() {
        return bayDoorOpen;
    }

    /**
     * Helper methods to manage agent capacity and state
     *
     * @param agentCapacity The amount of agent the drone can take
     */
    public void setAgentCapacity(double agentCapacity) {
        this.agentCapacity = agentCapacity;
    }
}

