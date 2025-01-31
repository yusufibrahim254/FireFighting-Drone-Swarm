package org.example.FireIncidentSubsystem.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;

public class ZoneReader {
    public ZoneReader(){

    }

    public Zone readZone(int zoneId, String fileName){
        // using the zone id, find the zone info
        try {
            // read the sample event file
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String header = reader.readLine(); // get the header so doesn't get turned into an event

            String line;
            while ((line = reader.readLine()) != null) { // breaks if reaches end of file
                String[] parts = line.split(",");

                if (Integer.parseInt(parts[0]) == zoneId){ // check if the current line is the zone id we are looking for
                    // if found, get the zone with its coordinates
                    String[] startCoords = parts[1].replace("(", "").replace(")", "").split(";");
                    int xCoordsOfStart = Integer.parseInt(startCoords[0]);
                    int yCoordsOfStart = Integer.parseInt(startCoords[1]);
                    Coordinates zoneStart = new Coordinates(xCoordsOfStart, yCoordsOfStart);

                    String[] endCoords = parts[2].replace("(", "").replace(")", "").split(";");
                    int xCoordsOfEnd = Integer.parseInt(endCoords[0]);
                    int yCoordsOfEnd = Integer.parseInt(endCoords[1]);
                    Coordinates zoneEnd = new Coordinates(xCoordsOfEnd, yCoordsOfEnd);
                    reader.close();

                    return new Zone(zoneId, zoneStart, zoneEnd);
                }

            }
            reader.close(); // close file
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
