package org.example;

import org.example.FireIncidentSubsystem.Event;

import java.util.Queue;
import java.util.LinkedList;

public class Scheduler implements Runnable {
    private boolean stop;
    private final Queue<Event> incidentQueue;

    /**
     * Constructs a new Scheduler with an empty incident queue.
     */
    public Scheduler() {
        this.incidentQueue = new LinkedList<>();
        this.stop = false;
    }

    /**
     * Checks if the scheduler is stopped.
     * @return true if the scheduler is stopped, false otherwise.
     */
    public boolean isStopped() {
        return stop;
    }

    /**
     * Retrieves and removes the next event from the queue.
     * @return the next Event, or null if the queue is empty.
     */
    public synchronized Event getEvent() {
        return incidentQueue.poll();
    }

    /**
     * Adds a fire incident event to the queue.
     * @param event the event to be added.
     */
    public synchronized void addIncident(Event event) {
        incidentQueue.add(event);
    }

    /**
     * Checks if the incident queue is empty.
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return incidentQueue.isEmpty();
    }

    /**
     * Runs the scheduler to process fire incidents and notify the DroneSubsystem.
     */
    @Override
    public void run() {
        while (!stop) {
            synchronized (this) {
                while (incidentQueue.isEmpty()) {
                    try {
                        wait();
                        System.out.println("Scheduler: Notifying fire incident events to DroneSubsystem");
                        System.out.println();
                        wait();
                        System.out.println();
                        System.out.println("Scheduler: Confirmed DroneSubsystem processed FireIncidentSubsystem events");
                        break;
                    } catch (InterruptedException e) {
                        System.out.println("Scheduler interrupted");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                stopScheduler();
            }
        }
    }

    /**
     * Stops the scheduler and notifies all waiting threads.
     */
    public void stopScheduler() {
        System.out.println("Scheduler stopped.");
        stop = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
