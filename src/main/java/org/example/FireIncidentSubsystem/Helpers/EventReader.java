package org.example.FireIncidentSubsystem.Helpers;

import org.example.FireIncidentSubsystem.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class EventReader {
    public EventReader(){

    }

    public static ArrayList<Event> readEvents(String fileName){
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
                events.add(event);
            }
            reader.close(); // close file
            return events;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
