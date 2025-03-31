package org.example.FireIncidentSubsystem.Helpers;

import static java.lang.Math.abs;

public class Zone {
    private int zoneId;
    private int width;
    private int length;
    private Coordinates zoneStart;
    private Coordinates zoneEnd;

    /**
     * Zone constructor
     * @param zoneId the id of the zone
     * @param zoneStart the starting coordinates of the zone
     * @param zoneEnd the ending coordinates of the zone
     */
    public Zone(int zoneId, Coordinates zoneStart, Coordinates zoneEnd) {
        this.zoneId = zoneId;
        this.zoneStart = zoneStart;
        this.zoneEnd = zoneEnd;
    }


    /**
     * Gets the zone id of the current zone
     * @return the zone id
     */
    public int getZoneId() {
        return zoneId;
    }

    /**
     * Gets the zone starting coordinates
     * @return the starting coordinates of the zone
     */
    public Coordinates getZoneStart() {
        return zoneStart;
    }

    /**
     * Gets the zone ending coordinates
     * @return the ending coordinates of the zone
     */
    public Coordinates getZoneEnd() {
        return zoneEnd;
    }

    public int getWidth() {
        return abs(getZoneStart().getX() - getZoneEnd().getX());
    }

    public int getLength() {
        return abs(getZoneStart().getY() - getZoneEnd().getY());
    }

    /**
     * Creates a string verison of the current zone
     * @return string version of the zone
     */
    @Override
    public String toString() {
        return zoneId + "," + zoneStart + "," + zoneEnd;
    }
}
