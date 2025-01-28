package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;

public class Event {
    private String time;
    private int zoneId;
    private final EventType eventType;
    private String severityLevel;

    public Event(String time, int zoneId, EventType eventType, String severityLevel) {
        this.time = time;
        this.zoneId = zoneId;
        this.eventType = eventType;
        this.severityLevel = severityLevel;
    }

    public String getTime() {
        return time;
    }

    public int getZoneId(){
        return zoneId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSeverity() {
        return severityLevel;
    }

    @Override
    public String toString() {
        return time + "," + zoneId + "," + eventType + "," + severityLevel ;
    }
}
