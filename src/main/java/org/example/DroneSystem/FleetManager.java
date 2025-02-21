package org.example.DroneSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FleetManager {

    /**
     * List of drones managed by this subsystem.
     */
    private final List<Drone> drones;

    /**
     * Constructs a DroneSubsystem with a given scheduler and initializes drones.
     *
     */
    public FleetManager() {
        this.drones = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            drones.add(new Drone(i, 15));
        }
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public boolean hasIdleDrones(){
        for (Drone drone: drones){
            if (Objects.equals(drone.getState(), new IdleState())) return true;
        }
        return false;
    }
}
