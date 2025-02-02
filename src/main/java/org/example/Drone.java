package org.example;

import org.example.FireIncidentSubsystem.Event;

public class Drone{

    private int id;
    private int battery;
    private double agentCapacity;
    private final double maxAgentCapacity;
    private boolean bayDoorOpen;
    private DroneState state;



    public Drone(int id, double initialCapacity) {
        this.id = id;
        this.battery = 100;
        this.agentCapacity = initialCapacity;
        this.maxAgentCapacity = initialCapacity;
        this.bayDoorOpen = false;
        this.state = DroneState.IDLE;
    }


    public double processEvent(Event event, double waterNeeded) {
        state = DroneState.DROPPING_AGENT;
        if (agentCapacity > waterNeeded){
            agentCapacity = agentCapacity - waterNeeded;
            System.out.println("Drone released water agent, " + waterNeeded + " litres released");
            state = DroneState.IDLE;
            return 0.0;
        }
        System.out.println("Drone released water agent, " + agentCapacity + " litres released");
        waterNeeded = waterNeeded - agentCapacity;
        agentCapacity = 0.0;
        state = DroneState.IDLE;
        return waterNeeded;
        //I need to wait to see what is going on to see if i need this
    }

    public void openBayDoors() throws InterruptedException {
        Thread.sleep(3000);
        bayDoorOpen = true;
        System.out.println("Bay doors opened.");
    }

    public void closeBayDoors() throws InterruptedException {
        Thread.sleep(3000);
        bayDoorOpen = false;
        System.out.println("Bay doors closed.");
    }

    public void refill() {
        state = DroneState.REFILLING;
        System.out.println("Drone is refilling");
        agentCapacity = maxAgentCapacity;
    }

    public DroneState getState() {
        return state;
    }

    public double getAgentCapacity() {
        return agentCapacity;
    }

    public void setState(DroneState state) {
        this.state = state;
    }

    public boolean isBayDoorOpen() {
        return bayDoorOpen;
    }
}
