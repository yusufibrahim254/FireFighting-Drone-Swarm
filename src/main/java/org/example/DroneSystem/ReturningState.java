package org.example.DroneSystem;

/**
 * State class represents the drone is returning to its original location after refilling.
 */
public class ReturningState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is returning. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone " + drone.getId() + " has returned to its original location.");
        try {
            // Wait 1 second before becoming operational again
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Transition to RefillingState to refill the drone
        drone.setState(new RefillingState());
    }

    @Override
    public double dropAgent(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is returning. Cannot drop agent.");
        return 0;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone " + drone.getId() + " is returning. Cannot refill.");
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
        System.out.println("Drone " + drone.getId() + " is RETURNING to its original location.");
    }

    @Override
    public String getState(Drone drone) {
        return "RETURNING";
    }
}
