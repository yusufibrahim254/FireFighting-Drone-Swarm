package org.example.DroneSystem;

/**
 * State class represents the drone is on its way to a fire incident zone.
 */
public class EnRouteState implements DroneState {

    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is already en route.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("\n\nDrone " + drone.getId() + " arrived and state is set to DROPPING AGENT.");
        drone.setState(new DroppingAgentState());
    }

    @Override
    public double dropAgent(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is en route. Cannot drop agent yet.");
        return 0;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is en route. Cannot refill now.");
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
        System.out.println("Drone " + drone.getId() + " is EN ROUTE.");
    }

    @Override
    public String getState(Drone drone) {
        return "EN_ROUTE";
    }
}
