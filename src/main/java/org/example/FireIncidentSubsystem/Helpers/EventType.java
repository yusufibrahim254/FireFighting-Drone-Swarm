package org.example.FireIncidentSubsystem.Helpers;

/**
 * Represents the different types of events that can be sent
 * to the subsystems in the FireFighting Drone Swarm
 */
public enum EventType {
    FIRE_DETECTED,
    DRONE_REQUEST,
    INVALID_FIRE_EVENT,
    DRONE_STUCK,
    NOZZLE_JAMMED,
    CORRUPTED_MESSAGE
}
