package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

/**
 * The Drone class models a firefighting drone that assists in fighting fires.
 * It is responsible for handling its own state transitions and printing its current state.
 */
public class Drone implements Runnable {
    private final int id; // Unique identifier for the drone
    private final double maxAgentCapacity; // Maximum amount of agent the drone can carry
    private double agentCapacity; // Current amount of agent in the drone
    private DroneState state; // Current state of the drone
    private final BayController bayController; // Controller for the drone's bay doors
    private Event currentEvent; // Current event assigned to the drone

    /**
     * Constructs a Drone with the specified ID and initial agent capacity.
     *
     * @param id The unique identifier of the drone.
     * @param initialCapacity The initial amount of agent the drone can carry.
     */
    public Drone(int id, double initialCapacity) {
        this.id = id;
        this.agentCapacity = initialCapacity;
        this.maxAgentCapacity = initialCapacity;
        this.bayController = new BayController();
        this.state = new IdleState();
    }

    /**
     * Runs the drone. Handles state transitions and simulates realistic timings.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Handle state transitions
                if (state instanceof IdleState) {
                    // Do nothing, wait for an event
                } else if (state instanceof EnRouteState) {
                    state.displayState();
                    simulateTravel();
                    state.arrive(this); // Transition to DroppingAgentState
                } else if (state instanceof DroppingAgentState) {
                    state.displayState();
                    double waterNeeded = currentEvent.getSeverityWaterAmount();
                    state.dropAgent(this, waterNeeded); // Drop agent and handle refill if needed
                } else if (state instanceof RefillingState) {
                    state.displayState();
                    state.refill(this); // Refill the drone
                } else if (state instanceof ReturningState) {
                    state.displayState();
                    simulateTravel(); // Simulate traveling back to the original location
                    state.arrive(this); // Transition to IdleState

                } else if (state instanceof FaultedState) {
                    // Handle fault state
                }
//                // Sleep to simulate real-time behavior
//                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assigns an event to the drone and transitions it to the EnRouteState.
     *
     * @param event The event to be assigned.
     */
    public void assignEvent(Event event) {
        this.currentEvent = event;
        System.out.println("Drone " + id + " assigned event: " + event);
        state.dispatch(this); // Transition to EnRouteState
    }

    /**
     * Simulates the drone traveling to a zone.
     *
     * @throws InterruptedException If the thread is interrupted while sleeping.
     */
    private void simulateTravel() throws InterruptedException {
        if(this.state instanceof EnRouteState){
            System.out.println("Drone " + id + " is traveling to zone: " + this.currentEvent.toString());
        }
        Thread.sleep(8180); // Simulate 8.18 seconds of travel time
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public double getMaxAgentCapacity(){
        return maxAgentCapacity;
    }
    public double getAgentCapacity() {
        return agentCapacity;
    }

    public void setAgentCapacity(double agentCapacity) {
        this.agentCapacity = agentCapacity;
    }

    public DroneState getState() {
        return state;
    }

    public void setState(DroneState state) {
        this.state = state;
    }

    public BayController getBayController() {
        return bayController;
    }
}