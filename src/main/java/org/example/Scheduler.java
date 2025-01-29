package org.example;

import org.example.FireIncidentSubsystem.Event;

public class Scheduler {
    private boolean stop;
    private Event event;



    public boolean isStopped() {
        return stop;
    }

    public Event getEvent() {
        return event;
    }
}
