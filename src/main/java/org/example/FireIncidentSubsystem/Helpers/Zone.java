package org.example.FireIncidentSubsystem.Helpers;

import static java.lang.Math.abs;

public class Zone {
    private final int zoneId;
    private final Coordinates zoneStart;
    private final Coordinates zoneEnd;

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

    /**
     * Get the width of the zone
     * @return the zone width
     */
    public int getWidth() {
        return abs(getZoneStart().getXCoords() - getZoneEnd().getXCoords());
    }

    /**
     * Get the length of the zone
     * @return the zone length
     */
    public int getLength() {
        return abs(getZoneStart().getYCoords() - getZoneEnd().getYCoords());
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
