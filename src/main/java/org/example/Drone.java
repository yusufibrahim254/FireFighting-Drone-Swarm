package org.example;

import org.example.FireIncidentSubsystem.Event;

public class Drone implements Runnable{
    private final Scheduler scheduler;

    public Drone(Scheduler scheduler) {
        this.scheduler = scheduler;
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
}
