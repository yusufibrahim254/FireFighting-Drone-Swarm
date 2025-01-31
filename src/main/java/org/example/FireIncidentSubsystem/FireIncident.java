package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;
import org.example.Scheduler;

public class FireIncident implements Runnable {
    private final static String eventFile = "Sample_event_file.csv";
    private final static String zoneFile = "sample_zone_file.csv";
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
    }

}
