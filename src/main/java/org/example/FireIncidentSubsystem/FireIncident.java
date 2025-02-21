package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;
import org.example.Scheduler;

/**
 * The Fire Incident subsystem processes fire events from an input file and sends to the
 * Scheduler to have firefighting drones assigned to handle event
 */
public class FireIncident implements Runnable {
    /**
     * The input file with all fire events within it
     */
    private final static String eventFile = "docs/Sample_event_file.csv";
    /**
     * The input file with all the zones in which fire taking place in within it
     */
    private final static String zoneFile = "docs/sample_zone_file.csv";
    /**
     * The scheduler to process fire events
     */
    private final Scheduler scheduler;

    /**
     * Constructs a new Fire Incident subsystem with a reference to the Scheduler.
     * @param scheduler the Scheduler
     */
    public FireIncident(Scheduler scheduler){
        this.scheduler = scheduler;
    }


    /**
     * Runs the fire incident subsystem to process fire events and notify the Scheduler.
     */
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