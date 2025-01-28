package org.example;

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
