package org.example;

import org.example.FireIncidentSubsystem.Event;

public class Drone implements Runnable{
    private final Scheduler scheduler;
    private double agentCapacity;
    private final double maxAgentCapacity;
    private boolean bayDoorOpen;

    public Drone(Scheduler scheduler, double initialCapacity) {
        this.scheduler = scheduler;
        this.agentCapacity = initialCapacity;
        this.maxAgentCapacity = initialCapacity;
        this.bayDoorOpen = false;
    }

    @Override
    public void run() {
        while (!scheduler.isStopped()) {
            Event event = scheduler.getEvent();
            if (event != null) {
                processEvent(event);
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    System.out.println("Drone was interrupted while processing an event.");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void processEvent(Event event) {
        System.out.println("Drone processing event: " + event);
        //I need to wait to see what is going on to see if i need this
    }

    private void openBayDoors() {
        bayDoorOpen = true;
        System.out.println("Bay doors opened.");
    }

    private void closeBayDoors() {
        bayDoorOpen = false;
        System.out.println("Bay doors closed.");
    }
}
