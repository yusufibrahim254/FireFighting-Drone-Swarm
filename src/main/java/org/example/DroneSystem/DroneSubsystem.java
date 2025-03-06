package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.Zones;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DroneSubsystem class manages a fleet of drones and assigns tasks to the closest available drone.
 */
public class DroneSubsystem implements Runnable {
    private final DatagramSocket schedulerSocket; // Socket for Scheduler communication
    private final DatagramSocket droneSocket; // Socket for Drone communication
    private final List<Drone> drones;
    private final Zones zones;

    public DroneSubsystem(int schedulerPort, int dronePort, String zonesFilePath) throws SocketException {
        this.schedulerSocket = new DatagramSocket(schedulerPort);
        this.droneSocket = new DatagramSocket(dronePort);
        this.drones = new ArrayList<>();
        this.zones = new Zones(zonesFilePath);

        // Initialize the fleet with 10 drones
        for (int i = 1; i <= 10; i++) {
            drones.add(new Drone(i, 15));
        }

        // Start each drone in a separate thread
        for (Drone drone : drones) {
            new Thread(drone).start();
        }
    }

    @Override
    public void run() {
        System.out.println("[DroneSubsystem] Listening on Ports: " + schedulerSocket.getLocalPort() + " (Scheduler), " + droneSocket.getLocalPort() + " (Drones)");

        // Start a thread to handle Scheduler messages
        new Thread(this::handleSchedulerMessages).start();

        // Start a thread to handle Drone location updates
        new Thread(this::handleDroneLocationUpdates).start();
    }

    /**
     * Handles messages from the Scheduler.
     */
    private void handleSchedulerMessages() {
        while (true) {
            try {
                // Receive event from Scheduler
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                schedulerSocket.receive(receivePacket);
                String eventData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("[DroneSubsystem] Received event from Scheduler: " + eventData);

                // Deserialize the event
                Event event = Event.deserialize(eventData);

                // Check if the zone ID is valid
                if (!zones.isZoneValid(event.getZoneId())) {
                    System.err.println("Invalid zone ID: " + event.getZoneId());
                    continue; // Skip processing for invalid zones
                }

                // Assign the event to the closest idle drone
                assignEventToClosestIdleDrone(event);

                // Send acknowledgment back to Scheduler
                String ack = "ACK:" + event.getId();
                byte[] sendData = ack.getBytes();
                InetAddress schedulerAddress = receivePacket.getAddress();
                int schedulerPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                schedulerSocket.send(sendPacket);
                System.out.println("[DroneSubsystem -> Scheduler] Sent acknowledgment: " + ack + "\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles location updates from the Drones.
     */
    private void handleDroneLocationUpdates() {
        while (true) {
            try {
                // Receive location update from a Drone
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                droneSocket.receive(receivePacket);
                String locationData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("[DroneSubsystem] Received location update: " + locationData);

                // Process the location update (e.g., update the drone's position in the subsystem)
                processDroneLocationUpdate(locationData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes a location update from a Drone.
     *
     * @param locationData The location data in the format "droneId,x,y".
     */
    private void processDroneLocationUpdate(String locationData) {
        String[] parts = locationData.split(",");
        if (parts.length == 3) {
            int droneId = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            // Update the drone's position
            for (Drone drone : drones) {
                if (drone.getId() == droneId) {
                    drone.setCurrentPosition(new int[]{x, y});
                    break;
                }
            }
        } else {
            System.err.println("Invalid location data format: " + locationData);
        }
    }

    /**
     * Assigns an event to the closest idle drone.
     *
     * @param event The event to be assigned.
     */
    private void assignEventToClosestIdleDrone(Event event) {
        int[] eventLocation = zones.getZoneMidpoint(event.getZoneId());
        Drone closestDrone = getClosestDrone(eventLocation);

        if (closestDrone != null && closestDrone.getState() instanceof IdleState) {
            closestDrone.setTargetPosition(eventLocation); // Set the target position
            closestDrone.setIncidentPosition(eventLocation);
            closestDrone.setCurrentEvent(event); // Assign the event
        }
    }

    public Zones getZones() {
        return zones;
    }

    /**
     * Finds the closest idle drone to the given location.
     *
     * @param location The target location.
     * @return The closest idle drone, or null if no idle drones are available.
     */
    private Drone getClosestDrone(int[] location) {
        Drone closestDrone = null;
        double minDistance = Double.MAX_VALUE;

        for (Drone drone : drones) {
            if (drone.getState() instanceof IdleState) {
                int[] dronePosition = drone.getCurrentPosition();
                double distance = Math.sqrt(Math.pow(location[0] - dronePosition[0], 2) + Math.pow(location[1] - dronePosition[1], 2));

                if (distance < minDistance) {
                    minDistance = distance;
                    closestDrone = drone;
                }
            }
        }

        return closestDrone;
    }

    public static void main(String[] args) {
        try {
            DroneSubsystem droneSubsystem = new DroneSubsystem(6000, 6001, "docs/sample_zone_file.csv");
            System.out.println("The zones are:\n" + droneSubsystem.getZones());
            droneSubsystem.run();
        } catch (SocketException e) {
            System.err.println("Error starting DroneSubsystem: " + e.getMessage());
        }
    }
}