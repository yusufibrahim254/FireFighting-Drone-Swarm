package org.example;

import org.example.FireIncidentSubsystem.Event;

import java.util.Queue;
import java.util.LinkedList;

public class Scheduler implements Runnable{
    private boolean stop;
    private final Queue<Event> incidentQueue;

    public Scheduler() {
        this.incidentQueue = new LinkedList<>();
        this.stop = false;
    }

    public boolean isStopped() {
        return stop;
    }

    public synchronized Event getEvent() {
        return incidentQueue.poll();
    }

    public synchronized void addIncident(Event event){
        incidentQueue.add(event);
    }

    public boolean isEmpty(){
        return incidentQueue.isEmpty();
    }

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

    public void stopScheduler() {
        System.out.println("Scheduler stopped.");
        stop = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
