package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;

public class Event {
    private String time;
    private Zone zone;
    private final EventType eventType;
    private Severity severity;

    public Event(String time, Zone zone, EventType eventType, Severity severity) {
        this.time = time;
        this.zone = zone;
        this.eventType = eventType;
        this.severity = severity;
    }

    public String getTime() {
        return time;
    }

    public Zone getZone(){
        return zone;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Severity getSeverity() {
        return severity;
    }
}
