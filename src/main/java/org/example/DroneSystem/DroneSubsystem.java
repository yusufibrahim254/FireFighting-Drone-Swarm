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
        for (int i = 1; i <= 2; i++) {
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
                // Receive the message from a Drone
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                droneSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("[DroneSubsystem] Received message: " + message);

                // Split the message into type and data (droneID or location update data)
                String[] parts = message.split(":");
                if (parts.length == 2) {
                    String type = parts[0];  // This will be the message type (e.g., "JOB_DELEGATION", "LOCATION_UPDATE")
                    String data = parts[1];  // This will be the drone ID or location data

                    // Check the type and process accordingly
                    if (type.equals(CommunicationDroneToSubsystem.JOB_DELEGATION.name())) {
                        // Handle job delegation
                        System.out.println("[DroneSubsystem] Job delegation received. Drone ID: " + data);
                        int droneId = Integer.parseInt(data);
                        Event droneEvent = drones.get(droneId -1).getCurrentEvent();
                        assignEventToClosestIdleDrone(droneEvent);
                    } else if (type.equals(CommunicationDroneToSubsystem.LOCATION_UPDATE.name())) {
                        // Handle location update
                        System.out.println("[DroneSubsystem] Location update received. Data: " + data);
                        processDroneLocationUpdate(data); // Call your method to handle location update
                    } else {
                        System.out.println("[DroneSubsystem] Unknown message type: " + type);
                    }
                } else {
                    System.out.println("[DroneSubsystem] Invalid message format.");
                }
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
            int droneId = Integer.parseInt(parts[0]) -1; // because the drone are initialized starting with index 1 instead of 0
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);

            // Update the drone's position
            drones.get(droneId).setCurrentPosition(new int[]{x, y});
//            for (Drone drone : drones) {
//                if (drone.getId() == droneId) {
//                    drone.setCurrentPosition(new int[]{x, y});
//                    break;
//                }
//            }
        } else {
            System.err.println("Invalid location data format: " + locationData);
        }
    }

    /**
     * Assigns an event to the closest idle drone or reassigns a drone en route to a same-severity incident.
     *
     * @param event The event to be assigned.
     */
    private void assignEventToClosestIdleDrone(Event event) {
        int[] eventLocation = zones.getZoneMidpoint(event.getZoneId());
        Drone currentAssignee = event.getAssignedDrone();
        Drone closestIdleDrone = null;
        Drone closestEnRouteDrone = null;
        Drone closestReturningDrone = null;
        double minIdleDistance = Double.MAX_VALUE;
        double minEnRouteDistance = Double.MAX_VALUE;
        double minReturningDistance = Double.MAX_VALUE;

        // Find the closest idle drone, en route drone with the same severity, and returning drone
        for (Drone drone : drones) {
            double distance = calculateDistance(drone, eventLocation);

            if (drone.getState() instanceof IdleState && distance < minIdleDistance) {
                minIdleDistance = distance;
                closestIdleDrone = drone;
            } else if (drone.getState() instanceof EnRouteState && isDroneEnRouteToSameSeverity(drone, event.getSeverityLevel()) && distance < minEnRouteDistance) {
                minEnRouteDistance = distance;
                closestEnRouteDrone = drone;
            } else if (drone.getState() instanceof ReturningState && distance < minReturningDistance) {
                minReturningDistance = distance;
                closestReturningDrone = drone;
            }
        }

        // 1. If a matching en route drone is found, reassign it to the new event and give its previous event to the closest idle drone
        if (closestEnRouteDrone != null && minEnRouteDistance < minIdleDistance) {
            Event originalEvent = closestEnRouteDrone.getCurrentEvent();

            // Reassign the en route drone to the new event
            closestEnRouteDrone.setTargetPosition(eventLocation);
            closestEnRouteDrone.setIncidentPosition(eventLocation);
            closestEnRouteDrone.setCurrentEvent(event);

            // Assign the original event to the closest idle drone
            if (closestIdleDrone != null) {
                closestIdleDrone.setTargetPosition(zones.getZoneMidpoint(originalEvent.getZoneId()));
                closestIdleDrone.setIncidentPosition(zones.getZoneMidpoint(originalEvent.getZoneId()));
                closestIdleDrone.setCurrentEvent(originalEvent);
            }
            return;
        }
        // 2. If no matching en route drone was found, assign the event to the closest idle drone
        else if (closestIdleDrone != null) {
            closestIdleDrone.setTargetPosition(eventLocation);
            closestIdleDrone.setIncidentPosition(eventLocation);

            Event previousEvent = closestIdleDrone.getCurrentEvent();
            if(currentAssignee != null){
                currentAssignee.setCurrentEvent(null);
            }
            event.setAssignedDrone(closestIdleDrone);
            closestIdleDrone.setCurrentEvent(event);
            return;
        }
        // 3. If there are no idle drones, assign the event to the closest returning drone
        else if (closestReturningDrone != null) {
            closestReturningDrone.setTargetPosition(eventLocation);
            closestReturningDrone.setIncidentPosition(eventLocation);
            closestReturningDrone.setCurrentEvent(event);
            return;
        }else{ // No drones available
//            System.out.println("NO DRONE AVAILABLE -------------------------");
//            // At this point the drone is returning back to 0,0, so if the job is not done, change it's target location
//            if(event.getCurrentWaterAmountNeeded() > 0){
//                currentAssignee.setTargetPosition(zones.getZoneMidpoint(event.getZoneId()));
//            }
        }
    }


    public Zones getZones() {
        return zones;
    }

    /**
     * Checks if a drone is en route to an incident with the same severity.
     *
     * @param drone The drone to check.
     * @param severity The severity of the new incident.
     * @return True if the drone is en route to an incident with the same severity, false otherwise.
     */
    private boolean isDroneEnRouteToSameSeverity(Drone drone, String severity) {
        return drone.getState() instanceof EnRouteState &&
                drone.getCurrentEvent() != null &&
                drone.getCurrentEvent().getSeverityLevel().equals(severity);
    }

    /**
     * Calculates the Euclidean distance between a drone and an incident location.
     *
     * @param drone The drone.
     * @param location The incident location.
     * @return The distance between the drone and the incident location.
     */
    private double calculateDistance(Drone drone, int[] location) {
        int[] dronePosition = drone.getCurrentPosition();
        return Math.sqrt(Math.pow(location[0] - dronePosition[0], 2) + Math.pow(location[1] - dronePosition[1], 2));
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