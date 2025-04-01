package org.example.DroneSystem;

/**
 * Interface represents the various states a firefighting drone can be in.
 */
public interface DroneState {
    void dispatch(Drone drone);
    void arrive(Drone drone);
    double dropAgent(Drone drone) throws InterruptedException;
    void refill(Drone drone);
    void fault(Drone drone);
    void reset(Drone drone);
    void displayState(Drone drone);
}

