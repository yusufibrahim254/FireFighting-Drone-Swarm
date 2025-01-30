package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;

public class Event {
    private String time;
    private int zoneId;
    private final EventType eventType;
    private final String severityLevel;

    private static final int LOW_SEVERITY = 10;
    private static final int MODERATE_SEVERITY = 20;
    private static final int HIGH_SEVERITY = 30;

    /**
     * Event constructor
     * @param time the timestamp of the event
     * @param zoneId the id of the zone where the event is taking place in
     * @param eventType the type of event occuring
     * @param severityLevel the severity level
     */
    public Event(String time, int zoneId, EventType eventType, String severityLevel) {
        this.time = time;
        this.zoneId = zoneId;
        this.eventType = eventType;
        this.severityLevel = severityLevel;
    }

    /**
     * Get the timestamp of the event
     * @return the timestamp of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the zone id of the event
     * @return the zone id of the event
     */
    public int getZoneId(){
        return zoneId;
    }

    /**
     * Get the event type of the event
     * @return the type of event
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Get the severity level of the event
     * @return the severity level
     */
    public int getSeverity() {
        return switch (severityLevel) {
            case "Low" -> LOW_SEVERITY;
            case "Moderate" -> MODERATE_SEVERITY;
            case "High" -> HIGH_SEVERITY;
            default -> 0;
        };
    }

    @Override
    public String toString() {
        return time + "," + zoneId + "," + eventType + "," + severityLevel ;
    }
}
