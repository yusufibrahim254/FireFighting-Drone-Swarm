package org.example.DroneSystem;

/**
 * State class represents the drone has encountered a problem.
 */
public class FaultedState implements DroneState {
    /**
     * State class represents the drone has encountered a problem.
     */
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is in FAULT state. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is in FAULT state. Cannot arrive anywhere.");
    }

    @Override
    public double dropAgent(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is in FAULT state. Cannot drop agent.");
        return 0;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is in FAULT state. Cannot refill.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is already in FAULT state.");
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone " + drone.getId() + ". Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is FAULTED. Needs reset.");
    }

    @Override
    public String getState(Drone drone) {
        return "FAULTED";
    }
}
