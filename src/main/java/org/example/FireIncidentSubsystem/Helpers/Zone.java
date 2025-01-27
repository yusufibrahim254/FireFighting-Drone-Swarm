package org.example.FireIncidentSubsystem.Helpers;

public class Zone {
    private int zoneId;
    private Coordinates zoneStart;
    private Coordinates zoneEnd;

    public Zone(int zoneId, Coordinates zoneStart, Coordinates zoneEnd) {
        this.zoneId = zoneId;
        this.zoneStart = zoneStart;
        this.zoneEnd = zoneEnd;
    }

    public int getZoneId() {
        return zoneId;
    }

    public Coordinates getZoneStart() {
        return zoneStart;
    }

    public Coordinates getZoneEnd() {
        return zoneEnd;
    }
}
