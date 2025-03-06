package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.*;

/**
 * Event object containing the fire incident event, such as when it occurred,
 * the severity of the event, etc.
 */
public class Event {
    private final int id; // Unique identifier for the event
    private String time; // Timestamp of the event
    private int zoneId; // ID of the zone where the event is taking place
    private final EventType eventType; // Type of event (e.g., FIRE)
    private final String severityLevel; // Severity level of the event

    /**
     * Event constructor
     *
     * @param id           The unique identifier for the event.
     * @param time         The timestamp of the event.
     * @param zoneId       The ID of the zone where the event is taking place.
     * @param eventType    The type of event occurring.
     * @param severityLevel The severity level of the event.
     */
    public Event(int id, String time, int zoneId, EventType eventType, String severityLevel) {
        this.id = id;
        this.time = time;
        this.zoneId = zoneId;
        this.eventType = eventType;
        this.severityLevel = severityLevel;
    }

    /**
     * Get the unique identifier of the event.
     *
     * @return The ID of the event.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the timestamp of the event.
     *
     * @return The timestamp of the event.
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the zone ID of the event.
     *
     * @return The zone ID of the event.
     */
    public int getZoneId() {
        return zoneId;
    }

    /**
     * Get the event type of the event.
     *
     * @return The type of event.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Get the severity level of the event.
     *
     * @return The severity level.
     */
    public String getSeverityLevel() {
        return severityLevel;
    }

    /**
     * Get the amount of water needed based on the severity level.
     *
     * @return The amount of water needed.
     */
    public int getSeverityWaterAmount() {
        return new Severity(severityLevel).getWaterAmount();
    }

    /**
     * Serializes the Event object into a string representation.
     * The format is: "id,time,zoneId,eventType,severityLevel"
     *
     * @return A string representation of the Event object.
     */
    public String serialize() {
        return id + "," + time + "," + zoneId + "," + eventType + "," + severityLevel;
    }

    /**
     * Deserializes a string representation of an Event back into an Event object.
     * The expected format is: "id,time,zoneId,eventType,severityLevel"
     *
     * @param data The string representation of the Event.
     * @return A new Event object.
     */
    public static Event deserialize(String data) {
        System.out.println("The data in deserialize function is "+data);
        String[] parts = data.split(",");
        int id = Integer.parseInt(parts[0]);
        String time = parts[1];
        int zoneId = Integer.parseInt(parts[2]);
        EventType eventType = EventType.valueOf(parts[3]);
        String severityLevel = parts[4];
        return new Event(id, time, zoneId, eventType, severityLevel);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", zoneId=" + zoneId +
                ", eventType=" + eventType +
                ", severityLevel='" + severityLevel + '\'' +
                '}';
    }
}