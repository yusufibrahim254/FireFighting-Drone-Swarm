package org.example.DroneSystem;

/**
 * State class representing the drone is idle and waiting for instructions
 */
public class IdleState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is dispatched and state is set to EN ROUTE.");
        drone.setState(new EnRouteState());
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is idle. It must be dispatched first.");
    }

    @Override
    public double dropAgent(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is idle. Cannot drop agent.");
        return 0;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is idle. No need to refill.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone " + drone.getId() + " encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is already idle.");
    }

    @Override
    public void displayState(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is in IDLE state.");
    }
}
