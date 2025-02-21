package org.example.FireIncidentSubsystem.Helpers;

import org.example.FireIncidentSubsystem.Event;
import org.example.Scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * A helper class that reads events from a CSV and organizes them into Event objects
 */
public class EventReader {

    /**
     * Constructor for event reader
     */
    public EventReader(){

    }

    /**
     * Reads fire events from provided file and organizes them into Event objects
     * @param fileName Input file with fire events
     * @param scheduler The scheduler to receive the events
     */
    public static void readEvents(String fileName, Scheduler scheduler){
        try {
            // read the sample event file
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String header = reader.readLine(); // get the header so doesn't get turned into an event

            ArrayList<Event> events = new ArrayList<>(); // store found events
            String line;
            while ((line = reader.readLine()) != null) { // breaks if reaches end of file
                String[] parts = line.split(",");

                // found attributes of the event
                String time = parts[0];
                int zoneId = Integer.parseInt(parts[1]);
                EventType eventType = EventType.valueOf(parts[2]);
                String severityLevel = parts[3];

                // create event
                Event event = new Event(time, zoneId, eventType, severityLevel);
                scheduler.addIncident(event);

                extracted(events, event);
            }
            reader.close(); // close file

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores found events into a list of Event objects
     * @param events storage list
     * @param event the event to add
     */
    private static void extracted(ArrayList<Event> events, Event event) {
        events.add(event);
    }

}
