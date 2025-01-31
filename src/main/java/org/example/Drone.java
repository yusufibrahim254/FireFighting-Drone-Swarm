package org.example;

import org.example.FireIncidentSubsystem.Event;

public class Drone{

    private double agentCapacity;
    private final double maxAgentCapacity;
    private boolean bayDoorOpen;
    private DroneState state;

    public enum DroneState {
        IDLE, EN_ROUTE, DROPPING_AGENT, REFILLING, FAULTED
    }

    public Drone(double initialCapacity) {
        this.agentCapacity = initialCapacity;
        this.maxAgentCapacity = initialCapacity;
        this.bayDoorOpen = false;
        this.state = DroneState.IDLE;
    }


    public void processEvent(Event event) {
        state = DroneState.DROPPING_AGENT;
        System.out.println("Drone released water agent, " + event.getSeverityWaterAmount() + " litres released");
        agentCapacity = agentCapacity - event.getSeverityWaterAmount();
        state = DroneState.IDLE;
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
        state = Drone.DroneState.REFILLING;
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
}
