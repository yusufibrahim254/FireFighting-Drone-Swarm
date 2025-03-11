package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

/**
 * Interface represents the various states a firefighting drone can be in.
 */
interface DroneState {
    void dispatch(Drone drone);
    void arrive(Drone drone);
    double dropAgent(Drone drone) throws InterruptedException;
    void refill(Drone drone);
    void fault(Drone drone);
    void reset(Drone drone);
    void displayState(Drone drone);
}

/**
 * State class representing the drone is idle and waiting for instructions
 */
class IdleState implements DroneState {
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

/**
 * State class represents the drone is on its way to a fire incident zone.
 */
class EnRouteState implements DroneState {

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
}

/**
 * State class represents the drone is currently dropping agent.
 */
class DroppingAgentState implements DroneState {
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
        System.out.println("Dropping "+ releasedWater+ "L  of agent...");
        drone.setAgentCapacity(drone.getAgentCapacity() - releasedWater);
        double remainingWaterNeeded = waterNeeded - releasedWater;
        currentEvent.setCurrentWaterAmountNeeded(remainingWaterNeeded);

        System.out.println("Remaining water needed to finish off fire: " + remainingWaterNeeded);
        System.out.println("Drone " + drone.getId() + " released water agent, " + releasedWater + " litres released");

        drone.getBayController().closeBayDoors();

        if(remainingWaterNeeded > 0){ // Fire is not extinguished
            // Delegate the job for better efficiency
            drone.delegateJob();
        }else{
            // it means the fire is off, so remove the event from the drone
            drone.setCurrentEvent(null);
        }
        drone.setTargetPosition(new int[]{0,0});
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
}

/**
 * State class represents the drone is refilling its firefighting agent.
 */
class RefillingState implements DroneState {
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
        System.out.println("Drone " + drone.getId() + " has refilled.");

        if(drone.getCurrentEvent() != null && drone.getCurrentEvent().getCurrentWaterAmountNeeded() > 0){
            drone.setTargetPosition(drone.getIncidentPosition());
            drone.setState(new EnRouteState());
        }else{
            drone.setState(new IdleState());
            System.out.println("\nDrone "+drone.getId()+" is now in IDLE STATE");
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
        System.out.println("Drone "+drone.getId()+" is REFILLING its fire suppressant.");
    }
}

/**
 * State class represents the drone has encountered a problem.
 */
class FaultedState implements DroneState {
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
}

/**
 * State class represents the drone is returning to its original location after refilling.
 */
class ReturningState implements DroneState {
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
    public void refill(Drone drone) {System.out.println("Drone " + drone.getId() + " is returning. Cannot refill.");}

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
}