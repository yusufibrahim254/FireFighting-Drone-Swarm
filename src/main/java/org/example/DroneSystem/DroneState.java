package org.example.DroneSystem;

/**
 * Interface represents the various states a firefighting drone can be in.
 */
interface DroneState {
    void dispatch(Drone drone);
    void arrive(Drone drone);
    double dropAgent(Drone drone, double waterNeeded) throws InterruptedException;
    void refill(Drone drone);
    void fault(Drone drone);
    void reset(Drone drone);
    void displayState();
}

/**
 * State class representing the drone is idle and waiting for instructions
 */
class IdleState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone is dispatched and state is set to EN ROUTE.");
        drone.setState(new EnRouteState());
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone is idle. It must be dispatched first.");
    }

    @Override
    public double dropAgent(Drone drone, double waterNeeded) {
        System.out.println("Drone is idle. Cannot drop agent.");
        return waterNeeded;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone is idle. No need to refill.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Drone is already idle.");
    }

    @Override
    public void displayState() {
        System.out.println("Drone is in IDLE state.");
    }
}

/**
 * State class represents the drone is on its way to a fire incident zone.
 */
class EnRouteState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone is already en route.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone arrived and state is set to DROPPING AGENT.");
        drone.setState(new DroppingAgentState());
    }

    @Override
    public double dropAgent(Drone drone, double waterNeeded) {
        System.out.println("Drone is en route. Cannot drop agent yet.");
        return waterNeeded;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone is en route. Cannot refill now.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone. Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState() {
        System.out.println("Drone is EN ROUTE.");
    }
}

/**
 * State class represents the drone is currently dropping agent.
 */
class DroppingAgentState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone is currently dropping agent. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone is already at the fire zone.");
    }

    @Override
    public double dropAgent(Drone drone, double waterNeeded) throws InterruptedException {
        System.out.println("Dropping agent...");
        DroneEvent droneEvent = new DroneEvent(drone);

        // Release the minimum of the drone's available water or the amount needed to extinguish the fire
        double releasedWater = Math.min(drone.getAgentCapacity(), waterNeeded);
        drone.setAgentCapacity(drone.getAgentCapacity() - releasedWater);
        waterNeeded -= releasedWater;

        System.out.println("Drone released water agent, " + releasedWater + " litres released");
        System.out.println("Water needed to finish off fire " + waterNeeded);

        drone.getBayController().closeBayDoors();

        // Refill drone if there's more agent needed to extinguish the fire and the drone ran out of agent
        if (waterNeeded > 0 && drone.getAgentCapacity() == 0) {
            System.out.println("Drone needs to refill.");
            drone.setState(new RefillingState());
            droneEvent.refill();
        }

        return waterNeeded;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone must finish dropping agent first.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone. Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState() {
        System.out.println("Drone is DROPPING FIRE SUPPRESSANT.");
    }
}

/**
 * State class represents the drone is refilling its firefighting agent.
 */
class RefillingState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone is refilling. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone is refilling. Cannot arrive anywhere.");
    }

    @Override
    public double dropAgent(Drone drone, double waterNeeded) {
        System.out.println("Drone is refilling. Cannot drop agent.");
        return waterNeeded;
    }

    @Override
    public void refill(Drone drone) {
        // Refill the drone to max capacity
        drone.setAgentCapacity(drone.getMaxAgentCapacity());
        System.out.println("REFILLING state complete.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone encountered an issue! Moving to FAULT state.");
        drone.setState(new FaultedState());
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone. Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState() {
        System.out.println("Drone is REFILLING its fire suppressant.");
    }
}

/**
 * State class represents the drone has encountered a problem.
 */
class FaultedState implements DroneState {
    @Override
    public void dispatch(Drone drone) {
        System.out.println("Drone is in FAULT state. Cannot be dispatched.");
    }

    @Override
    public void arrive(Drone drone) {
        System.out.println("Drone is in FAULT state. Cannot arrive anywhere.");
    }

    @Override
    public double dropAgent(Drone drone, double waterNeeded) {
        System.out.println("Drone is in FAULT state. Cannot drop agent.");
        return waterNeeded;
    }

    @Override
    public void refill(Drone drone) {
        System.out.println("Drone is in FAULT state. Cannot refill.");
    }

    @Override
    public void fault(Drone drone) {
        System.out.println("Drone is already in FAULT state.");
    }

    @Override
    public void reset(Drone drone) {
        System.out.println("Resetting drone. Moving to Idle state.");
        drone.setState(new IdleState());
    }

    @Override
    public void displayState() {
        System.out.println("Drone is FAULTED. Needs reset.");
    }
}
