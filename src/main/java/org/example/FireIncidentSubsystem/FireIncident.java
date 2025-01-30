package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;
import java.util.ArrayList;

public class FireIncident implements Runnable {
    private final static String eventFile = "Sample_event_file.csv";
    private final static String zoneFile = "sample_zone_file.csv";

    public FireIncident(){

    }

    @Override
    public void run() {
        // testing thread
        ArrayList<Event> events = EventReader.readEvents(eventFile);
        if (events != null) {
            for (Event event : events) { // check to see what events are found
                // print for now, later implementation will send to scheduler
                System.out.println(event);

                Zone foundZone = ZoneReader.readZone(event.getZoneId(), zoneFile);
                System.out.println(foundZone);
            }
        }
    }

    // testing main
    public static void main(String[] args) {
        Thread fireThread = new Thread(new FireIncident());
        fireThread.start();
    }
}
