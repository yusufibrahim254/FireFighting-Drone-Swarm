package org.example.DroneSystem;

/**
 * State class represents the drone is refilling its firefighting agent.
 */
public class RefillingState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is refilling. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is refilling. Cannot arrive anywhere.");
    }

    @Override
    public double dropAgent(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is refilling. Cannot drop agent.");
        return 0;
    }

    @Override
    public void refill(Drone drone) {
        drone.setAgentCapacity(drone.getMaxAgentCapacity());
        drone.setBatteryLevel(100);
        System.out.println("Drone " + drone.getId() + " has refilled and recharged.");

        if (drone.getCurrentEvent() != null && drone.getCurrentEvent().getCurrentWaterAmountNeeded() > 0) {
            drone.setTargetPosition(drone.getIncidentPosition());
            drone.setState(new EnRouteState());
        } else {
            drone.setState(new IdleState());
            System.out.println("\nDrone " + drone.getId() + " is now in IDLE STATE");
        }
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
        System.out.println("Drone " + drone.getId() + " is REFILLING its fire suppressant.");
    }
}
