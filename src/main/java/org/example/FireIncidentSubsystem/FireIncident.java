package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;
import org.example.Scheduler;

public class FireIncident implements Runnable {
    private final static String eventFile = "docs/Sample_event_file.csv";
    private final static String zoneFile = "docs/sample_zone_file.csv";
    private final Scheduler scheduler;

    public FireIncident(Scheduler scheduler){
        this.scheduler = scheduler;
    }


    @Override
    public void run() {
        synchronized (scheduler) {
            EventReader.readEvents(eventFile, scheduler);
            System.out.println();
            System.out.println("FireIncidentSubsystem: Reporting events to Scheduler");
            System.out.println();
            scheduler.notifyAll();
        }

        // Wait for the scheduler to process all events
        while (!scheduler.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        // Stop the scheduler
        scheduler.stopScheduler();
        System.out.println("FireIncidentSubsystem: Stopped the Scheduler after processing all events.");
    }

}