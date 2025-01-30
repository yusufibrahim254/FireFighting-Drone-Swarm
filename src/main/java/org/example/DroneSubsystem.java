package org.example;

import org.example.FireIncidentSubsystem.Event;
import java.util.ArrayList;
import java.util.List;

public class DroneSubsystem implements Runnable {

    private final List<Drone> drones;
    private final Scheduler scheduler;

    public DroneSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.drones = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            drones.add(new Drone(15));
        }
    }

    public void assignDroneToEvent(Event event) {
        for (Drone drone : drones) {
            if (drone.getState() == Drone.DroneState.IDLE) {
                System.out.println("Assigning drone to event: ");
                drone.setState(Drone.DroneState.EN_ROUTE);
                System.out.println("Drone state set to EN_ROUTE");

                try {
                    drone.openBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error opening bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                drone.processEvent(event);

                try {
                    drone.closeBayDoors();
                } catch (InterruptedException e) {
                    System.out.println("Error closing bay doors: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                if (drone.getAgentCapacity() == 0) {
                    System.out.println("Drone needs to refill.");
                    drone.refill();
                }
                return;
            }

            System.out.println("no available drones");
        }
    }

    @Override
    public void run() {
        //guarantee it starts after fireincident thread, and since scheduler waits it will ensure fireincident->scheduler->drone
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (scheduler) {
            while (!scheduler.isEmpty()) {
                Event event = scheduler.getEvent();
                if (event != null) {
                    System.out.println("DroneSubsystem: Processing event - " + event);
                    assignDroneToEvent(event);
                    System.out.println();
                }
            }
            System.out.println("DroneSubsystem: Sending Confirmation back to Scheduler");
            scheduler.notifyAll();
        }
    }
}

