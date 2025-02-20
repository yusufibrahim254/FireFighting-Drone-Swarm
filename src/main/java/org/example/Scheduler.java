package org.example;

import org.example.DroneSystem.DroneSubsystem;
import org.example.FireIncidentSubsystem.Event;
import java.util.Queue;
import java.util.LinkedList;

/**
 * The Scheduler class manages fire incident events and assigns them to the Drone Subsystem.
 * It runs as a separate thread designed to handle and coordinate incoming fire incidents.
 */
public class Scheduler implements Runnable {

    /**
     * Flag to indicate whether the scheduler should stop running.
     */
    private boolean stop;

    /**
     * Queue holding various fire incident events to be processed by the drones.
     */
    private final Queue<Event> incidentQueue;

    /**
     * A reference to the DroneSubsystem that will handle each event.
     */
    private final DroneSubsystem droneSubsystem;

    /**
     * Constructs a new Scheduler with an empty incident queue and a reference to the DroneSubsystem.
     */
    public Scheduler(DroneSubsystem droneSubsystem) {
        this.incidentQueue = new LinkedList<>();
        this.stop = false;
        this.droneSubsystem = droneSubsystem;
    }

    /**
     * Checks if the scheduler is stopped.
     *
     * @return true if the scheduler is stopped, false otherwise.
     */
    public boolean isStopped() {
        return stop;
    }

    /**
     * Retrieves and removes the next event from the queue.
     *
     * @return the next Event, or null if the queue is empty.
     */
    public synchronized Event getEvent() {
        return incidentQueue.poll();
    }

    /**
     * Adds a fire incident event to the queue.
     *
     * @param event the event to be added.
     */
    public synchronized void addIncident(Event event) {
        incidentQueue.add(event);
        notifyAll(); // Notify the scheduler that a new event has been added
    }

    /**
     * Checks if the incident queue is empty.
     *
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
            Event nextEvent = null;

            // Wait for an event to be added to the queue
            synchronized (this) {
                while (incidentQueue.isEmpty() && !stop) {
                    try {
                        wait();  // Wait until we are notified of a new event
                    } catch (InterruptedException e) {
                        System.out.println("Scheduler interrupted");
                        Thread.currentThread().interrupt();
                        return; // We were interrupted, exit the run loop
                    }
                }
                // If we were woken up because we were told to stop, exit the run loop
                if (stop) {
                    break;
                }
                // else we were woken up because there is an event in the queue
                nextEvent = incidentQueue.poll();
            }

            // Process the next event if there is one
            if (nextEvent != null) {
                System.out.println("Scheduler: Notifying fire incident events to DroneSubsystem");
                System.out.println("Event taking place: " + nextEvent.toString());

                // call matching method in DroneSubsystem
                try {
                    droneSubsystem.assignDroneToEvent(nextEvent);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Scheduler: Confirmed DroneSubsystem processed FireIncidentSubsystem event\n");
                System.out.println();
            }
        }
        // Scheduler has stopped
        System.out.println("Scheduler: Stopped processing fire incident events.");
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