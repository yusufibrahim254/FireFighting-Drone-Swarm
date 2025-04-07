package org.example.FireIncidentSubsystem;

import org.example.DroneSystem.Drone;
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
    private String fault;
    private Drone assignedDrone;
    private long receivedTime;       // Time when scheduler first sees this event
    private long assignedTime;       // when DroneSubsystem assigns  drone
    private long extinguishedTime;   // Drone is done extinguishing the fire
    private double distanceTraveled; // distance traveled by the drone to the fire

    private double currentWaterAmountNeeded;
    /**
     * Event constructor
     *
     * @param id            The unique identifier for the event.
     * @param time          The timestamp of the event.
     * @param zoneId        The ID of the zone where the event is taking place.
     * @param eventType     The type of event occurring.
     * @param severityLevel The severity level of the event.
     * @param fault         The parameter indicating a drone's fault
     */
    public Event(int id, String time, int zoneId, EventType eventType, String severityLevel, String fault) {
        this.id = id;
        this.time = time;
        this.zoneId = zoneId;
        this.eventType = eventType;
        this.severityLevel = severityLevel;
        this.currentWaterAmountNeeded = new Severity(severityLevel).getWaterAmount();
        this.assignedDrone = null;
        this.fault = fault;
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
        return id + "," + time + "," + zoneId + "," + eventType + "," + severityLevel + "," + fault;
    }

    /**
     * Deserializes a string representation of an Event back into an Event object.
     * The expected format is: "id,time,zoneId,eventType,severityLevel"
     *
     * @param data The string representation of the Event.
     * @return A new Event object.
     */
    public static Event deserialize(String data) {
//        System.out.println("The data in deserialize function is "+data);
        System.out.println(data);
        String[] parts = data.split(",");
        int id = Integer.parseInt(parts[0]);
        String time = parts[1];
        int zoneId = Integer.parseInt(parts[2]);
        EventType eventType = EventType.valueOf(parts[3]);
        String severityLevel = parts[4];
        String fault = parts[5];
        return new Event(id, time, zoneId, eventType, severityLevel, fault);
    }

    /**
     * Checks if the event is a valid event
     * @param event the event
     * @return true if event is valid, false if otherwise
     */
    public boolean isValidEvent(Event event) {
        // check for a valid id
        if (event.getId() < 0) {
            System.out.println("[Validation] Invalid event ID: " + event.getId());
            return false;
        }

        // check for a valid zone id
        if (event.getZoneId() < 0) { // needs to point to a proper zone found in the zone CSV
            System.out.println("[Validation] Invalid zone ID: " + event.getZoneId());
            return false;
        }

        try { // try to get the EventType value to ensure its a valid event type
            EventType eventType = EventType.valueOf(event.getEventType().toString());
        } catch (IllegalArgumentException e) {
            System.out.println("[Validation] Invalid event type: " + event.getEventType());
            return false;
        }

        // check the severity level
        switch (event.getSeverityLevel().toLowerCase()) {
            case "low":
            case "moderate":
            case "high":
                break;
            default:
                System.out.println("[Validation] Invalid severity level: " + event.getSeverityLevel());
                return false;
        }
        // passes all previous checks
        return true;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", zoneId=" + zoneId +
                ", eventType=" + eventType +
                ", severityLevel='" + severityLevel + '\'' +
                ", fault=" + fault +
                '}';
    }

    /**
     * Gets the current amount of water needed for the event
     * @return the amount of water needed
     */
    public double getCurrentWaterAmountNeeded() {
        return currentWaterAmountNeeded;
    }

    /**
     * Sets the current amount of water needed for the event
     * @param currentWaterAmountNeeded the new amount of water needed
     */
    public void setCurrentWaterAmountNeeded(double currentWaterAmountNeeded) {
        this.currentWaterAmountNeeded = currentWaterAmountNeeded;
    }

    /**
     * Gets the event's assigned drone
     * @return the assigned drone
     */
    public Drone getAssignedDrone() {
        return assignedDrone;
    }

    /**
     * Sets the event's drone
     * @param assignedDrone the new drone for event
     */
    public void setAssignedDrone(Drone assignedDrone) {
        this.assignedDrone = assignedDrone;
    }
    public String getFault(){return this.fault;};

    public void setFault(String fault){
        this.fault = fault;
    }

    /**
     * Gets the time when the event was received
     * @return the time when the event was received
     */
    public long getReceivedTime() {
        return receivedTime;
    }

    /**
     * Sets the time when the event was received
     * @param receivedTime the time when the event was received
     */
    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * Gets the time when the event was assigned
     * @return the time when the event was assigned
     */
    public long getAssignedTime() {
        return assignedTime;
    }

    /**
     * Sets the time when the event was assigned
     * @param assignedTime the time when the event was assigned
     */
    public void setAssignedTime(long assignedTime) {
        this.assignedTime = assignedTime;
    }

    /**
     * Gets the time when the event was extinguished
     * @return the time when the event was extinguished
     */
    public long getExtinguishedTime() {
        return extinguishedTime;
    }

    /**
     * Sets the time when the event was extinguished
     * @param extinguishedTime the time when the event was extinguished
     */
    public void setExtinguishedTime(long extinguishedTime) {
        this.extinguishedTime = extinguishedTime;
    }

    /**
     * Gets the distance traveled by the drone to the fire
     * @return the distance traveled by the drone to the fire
     */
    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    /**
     * Sets the distance traveled by the drone to the fire
     * @param distanceTraveled the distance traveled by the drone to the fire
     */
    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

}