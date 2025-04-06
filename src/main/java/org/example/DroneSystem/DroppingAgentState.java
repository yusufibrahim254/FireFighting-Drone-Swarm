package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

/**
 * State class represents the drone is currently dropping agent.
 */
public class DroppingAgentState implements DroneState {
    private DroneSubsystem droneSubsystem;

    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is currently dropping agent. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is already at the fire zone.");
    }

    @Override
    public double dropAgent(Drone drone) throws InterruptedException {
        Event currentEvent = drone.getCurrentEvent();
        double waterNeeded = currentEvent.getCurrentWaterAmountNeeded();
        // Release the minimum of the drone's available water or the amount needed to extinguish the fire
        double releasedWater = Math.min(drone.getAgentCapacity(), waterNeeded);
        System.out.println("Dropping " + releasedWater + "L  of agent...");
        drone.setAgentCapacity(drone.getAgentCapacity() - releasedWater);
        double remainingWaterNeeded = waterNeeded - releasedWater;
        currentEvent.setCurrentWaterAmountNeeded(remainingWaterNeeded);

        System.out.println("Remaining water needed to finish off fire: " + remainingWaterNeeded + " litres");
        System.out.println("Drone " + drone.getId() + " released water agent, " + releasedWater + " litres released");

        drone.getBayController().closeBayDoors();

        if (remainingWaterNeeded > 0) { // Fire is not extinguished
            // Delegate the job for better efficiency
            drone.delegateJob();
        } else {
            // it means the fire is off, so remove the event from the drone
            currentEvent.setExtinguishedTime(System.currentTimeMillis()); // Set the time when the fire was extinguished
            droneSubsystem.markEventCompleted(currentEvent);
            System.out.println("Event completed, fire extinguished");
            drone.setCurrentEvent(null);
        }
        drone.setTargetPosition(new int[]{0, 0});
        drone.setState(new ReturningState());

        return remainingWaterNeeded; // Ensure the caller updates its waterNeeded value
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone " + drone.getId() + " must finish dropping agent first.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone " + drone.getId() + " encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone " + drone.getId() + ". Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is DROPPING FIRE SUPPRESSANT.");
    }

    @Override
    public String getState(Drone drone) {
        return "DROPPING_AGENT";
    }

    public DroppingAgentState(DroneSubsystem droneSubsystem) {
        this.droneSubsystem = droneSubsystem;
    }

}
