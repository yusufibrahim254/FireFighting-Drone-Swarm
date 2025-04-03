package org.example.FireIncidentSubsystem.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The Zones class represents a set of rectangular zones where fires are simulated.
 */
public class Zones {
    private final Map<Integer, int[]> zoneMap; // Maps zone IDs to their midpoint coordinates
    private LinkedList<Zone> zoneList = new LinkedList<>();

    /**
     * Constructor for zones
     * @param filePath the zones CSV
     */
    public Zones(String filePath) {
        this.zoneMap = new HashMap<>();
        loadZonesFromCSV(filePath);
    }

    /**
     * Loads zone data from a CSV file.
     *
     * @param filePath The path to the CSV file.
     */
    public LinkedList<Zone> loadZonesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true; // Flag to skip the header row
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header row
                    continue;
                }

                String[] values = line.split(",");
                if (values.length == 3) {
                    int zoneId = Integer.parseInt(values[0].trim());
                    String[] startCoords = values[1].replace("(", "").replace(")", "").split(";"); // Use semicolon as separator
                    String[] endCoords = values[2].replace("(", "").replace(")", "").split(";"); // Use semicolon as separator

                    int x1 = Integer.parseInt(startCoords[0].trim());
                    int y1 = Integer.parseInt(startCoords[1].trim());
                    int x2 = Integer.parseInt(endCoords[0].trim());
                    int y2 = Integer.parseInt(endCoords[1].trim());

                    int midX = (x1 + x2) / 2;
                    int midY = (y1 + y2) / 2;

                    Zone zone = new Zone(zoneId, new Coordinates(x1, y1), new Coordinates(x2, y2));
                    zoneMap.put(zoneId, new int[]{midX, midY});
                    zoneList.add(zone);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading zones file: " + e.getMessage());
        }
        return zoneList;
    }

    /**
     * Gets the midpoint coordinates of a zone.
     *
     * @param zoneId The ID of the zone.
     * @return The midpoint coordinates as an array [x, y].
     * @throws IllegalArgumentException If the zone ID is invalid.
     */
    public int[] getZoneMidpoint(int zoneId) {
        if (!zoneMap.containsKey(zoneId)) {
            throw new IllegalArgumentException("Invalid zone ID: " + zoneId);
        }
        return zoneMap.get(zoneId);
    }

    /**
     * Checks if a zone ID is valid.
     *
     * @param zoneId The ID of the zone.
     * @return True if the zone ID is valid, false otherwise.
     */
    public boolean isZoneValid(int zoneId) {
        return zoneMap.containsKey(zoneId);
    }

    /**
     * Gets a specific fire zones from provided file and a zone id
     * and organizes them into Zone objects
     * @param zoneId the id of the zone of interest
     * @param fileName the CSV file with all zones within it
     * @return a Zone object containing info for the zone of interest
     */
    public static Zone getZoneFromId(int zoneId, String fileName){
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Zones:\n");
        for (Map.Entry<Integer, int[]> entry : zoneMap.entrySet()) {
            int zoneId = entry.getKey();
            int[] midpoint = entry.getValue();
            sb.append("Zone ID: ").append(zoneId)
                    .append(", Midpoint: (").append(midpoint[0]).append(", ").append(midpoint[1]).append(")\n");
        }
        return sb.toString();
    }
    /**
     * Gets the furthest zone's midpoint from the origin (0, 0).
     *
     * @return The midpoint coordinates of the furthest zone as an array [x, y].
     */
    public int[] getFurthestZoneMidpoint() {
        int[] furthestMidpoint = null;
        double maxDistance = 0;

        for (int[] midpoint : zoneMap.values()) {
            double distance = Math.sqrt(Math.pow(midpoint[0], 2) + Math.pow(midpoint[1], 2));
            if (distance > maxDistance) {
                maxDistance = distance;
                furthestMidpoint = midpoint;
            }
        }
        return furthestMidpoint;
    }

    public LinkedList<Zone> getZoneList() {
        return zoneList;
    }
}