package org.example;

import org.example.DroneSystem.DroneSubsystem;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.example.FireIncidentSubsystem.Event;

class SchedulerTest {
    private Scheduler scheduler;
    private Event event;

    @BeforeEach
    void setUp() {
        DroneSubsystem droneSubsystem = new DroneSubsystem();
        scheduler = new Scheduler(droneSubsystem);
        event = new Event("12:12:12", 2, EventType.DRONE_REQUEST, "High");
    }

    @Test
    void isStopped() {
        assertFalse(scheduler.isStopped());
        scheduler.stopScheduler();
        assertTrue(scheduler.isStopped());
    }

    @Test
    void addIncident() {
        assertTrue(scheduler.isEmpty());
        scheduler.addIncident(event);
        assertFalse(scheduler.isEmpty());
        assertEquals(scheduler.getEvent(), event);
        assertTrue(scheduler.isEmpty());
    }

    @Test
    void getEvent() {
        scheduler.addIncident(event);
        assertEquals(scheduler.getEvent(), event);
        assertTrue(scheduler.isEmpty());
    }

    @Test
    void isEmpty() {
        assertTrue(scheduler.isEmpty());
        scheduler.addIncident(event);
        assertFalse(scheduler.isEmpty());
    }

    @Test
    void stopScheduler() {
        assertFalse(scheduler.isStopped());
        scheduler.stopScheduler();
        assertTrue(scheduler.isStopped());
    }
}