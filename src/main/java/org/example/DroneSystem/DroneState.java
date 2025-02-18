package org.example.DroneSystem;

/**
 * Represents the various states a firefighting drone can be in.
 */
public enum DroneState {
    /**
     * The drone is idle and waiting for instructions
     */
    IDLE,

    /**
     * The drone is on its way to a fire incident zone.
     */
    EN_ROUTE,

    /**
     * The drone is currently dropping agent.
     */
    DROPPING_AGENT,

    /**
     * The drone is refilling its firefighting agent.
     */
    REFILLING,

    /**
     * The drone has encountered a problem.
     */
    FAULTED
}