package org.example.FireIncidentSubsystem.Helpers;

import org.example.FireIncidentSubsystem.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * A helper class that reads events from a CSV and organizes them into Event objects.
 */
public class EventReader {

    /**
     * Constructor for EventReader.
     */
    public EventReader() {
        // Constructor is empty as no initialization is needed.
    }

    /**
     * Reads fire events from the provided file and organizes them into Event objects.
     *
     * @param fileName The input file containing fire events.
     * @return An array of Event objects representing the fire events.
     */
    public static Event[] readEvents(String fileName) {
        ArrayList<Event> events = new ArrayList<>(); // Store found events
        int nextEventId = 1; // Counter for generating unique event IDs

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String header = reader.readLine(); // Read the header so it doesn't get turned into an event

            String line;
            while ((line = reader.readLine()) != null) { // Read until the end of the file
                String[] parts = line.split(",");

                // Extract attributes of the event
                String time = parts[0];
                EventType eventType;
                int zoneId = Integer.parseInt(parts[1]);
                try {
                     eventType = EventType.valueOf(parts[2]);
                } catch (IllegalArgumentException e){
                    eventType = EventType.INVALID_FIRE_EVENT;
                }
                String severityLevel = parts[3];

                String fault = "NO_FAULT";
                if (parts.length > 4) {
                    fault = parts[4].trim();
                }

                // Create an event with a unique ID
                Event event = new Event(nextEventId++, time, zoneId, eventType, severityLevel, fault);
                events.add(event); // Add the event to the list

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert the list of events to an array and return it
        return events.toArray(new Event[0]);
    }
}