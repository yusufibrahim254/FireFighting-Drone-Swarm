package org.example.DroneSystem;

import org.example.DisplayConsole.*;
import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.Zones;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
    private ConsoleView consoleView;
    private ConsoleController consoleController;
    private DroneStatusViewer droneStatusViewer;
    private EventDashboard eventDashboard;
    private final List<Event> activeEvents = new ArrayList<>();
    private final List<Event> completedEvents = new ArrayList<>();
    private int totalEventsCount = 0;
    private int nextAssignmentIndex = 0; // Tracks where to start searching for available drones
    private final long simulationStartTime;

    /**
     * Constructor for the drone subsystem
     * @param schedulerPort the port of the scheduler
     * @param dronePort the port of the drones
     * @param zonesFilePath where the zones CSV is stored
     * @throws SocketException incorrect socket
     */
    public DroneSubsystem(int schedulerPort, int dronePort, String zonesFilePath) throws SocketException {
        this.schedulerSocket = new DatagramSocket(schedulerPort);
        this.droneSocket = new DatagramSocket(dronePort);
        this.drones = new ArrayList<>();
        this.zones = new Zones(zonesFilePath);
        this.simulationStartTime = System.currentTimeMillis();  // Record current time

        Home home = new Home();
        this.consoleView = home.getView();
        this.consoleController = consoleView.getController();
        this.consoleController.setDroneSubsystem(this);
        this.droneStatusViewer = home.getStatus();
        this.eventDashboard = home.getDashboard();

        // Calculate the furthest zone's midpoint
        int[] furthestMidpoint = zones.getFurthestZoneMidpoint();
        double distanceToFurthestZone = Math.sqrt(Math.pow(furthestMidpoint[0], 2) + Math.pow(furthestMidpoint[1], 2));
        System.out.println("Distance to furthestZone: "+ distanceToFurthestZone);
        double totalDistance = 4 * distanceToFurthestZone; // To and back twice
        double batteryDepletionRate = 100.0 / totalDistance; // Battery depletes by this amount per meter
        System.out.println("Battery Depletion Rate: "+ batteryDepletionRate);

        // Initialize the fleet with 10 drones
        for (int i = 1; i <= 10; i++) {
            Drone newDrone = new Drone(i, 15, this, batteryDepletionRate);
            drones.add(newDrone);
        }

        // Start each drone in a separate thread
        for (Drone drone : drones) {
            new Thread(drone).start();
        }
    }

    /**
     * Runs this operation.
     */
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
//                System.out.println("[DroneSubsystem] Received event from Scheduler: " + eventData);
                // Deserialize the event
                Event event = Event.deserialize(eventData);

                event.setReceivedTime(System.currentTimeMillis() - simulationStartTime);

//                // Check if the zone already has an event
                boolean zoneIsBusy = false;
                System.out.println("ABOUT TO CHECK THE ACTIVE EVENTS");
                for(Event currentEvent: activeEvents){
                    if(currentEvent.getZoneId() == event.getZoneId()){
                        zoneIsBusy = true;
                        break;

                    }
                }

                if (zoneIsBusy) {
                    // Send a message so it can be added to the back of the queue
                    String noAck = "ACK: ZONE_IS_BUSY";
                    byte[] sendData = noAck.getBytes();
                    InetAddress schedulerAddress = receivePacket.getAddress();
                    int schedulerPort = receivePacket.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                    schedulerSocket.send(sendPacket);
                    continue; // This will now work as intended
                }

                // Check if there are any idle drone, if not send a message back right away
//                System.out.println("The number of idle drones is "+ numberOfIdleDrones);
                if(getAvailableDrones(event) < 1){
                    String noAck = "ACK: NO";
                    byte[] sendData = noAck.getBytes();
                    InetAddress schedulerAddress = receivePacket.getAddress();
                    int schedulerPort = receivePacket.getPort();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                    schedulerSocket.send(sendPacket);
//                    System.out.println("[DroneSubsystem -> Scheduler] Sent acknowledgment: " + noAck);
                    continue;
                }


                // Check if the zone ID is valid
                if (!zones.isZoneValid(event.getZoneId())) {
                    System.err.println("Invalid zone ID: " + event.getZoneId());
                    continue; // Skip processing for invalid zones
                }

                // Assign the event to the closest idle drone
                assignEventToClosestIdleDrone(event);
                totalEventsCount++;

                consoleView.markFire(event.getZoneId());
                synchronized (activeEvents){
                    activeEvents.add(event);
                }
                eventDashboard.addFireEvent(event.getZoneId(), event.getSeverityWaterAmount());

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
     * Removes an event from the activeEvents list based on zone ID
     *
     * @param zoneId The zone ID of the event to remove
     * @return true if the event was successfully removed, false otherwise
     */
    public boolean removeEvent(int zoneId) {
        synchronized (activeEvents) {
            // Find the event with matching zone ID
            for (int i = 0; i < activeEvents.size(); i++) {
                Event event = activeEvents.get(i);
                if (event.getZoneId() == zoneId) {
                    // Remove the event from the list
                    Event removedEvent = activeEvents.remove(i);

                    // Clean up associated resources
                    consoleView.clearFireInZone(zoneId);
                    eventDashboard.removeFireEvent(zoneId);

                    // Clear drone assignment if exists
                    if (removedEvent.getAssignedDrone() != null) {
                        removedEvent.getAssignedDrone().setCurrentEvent(null);
                    }
                    return true;
                }
            }
            return false;
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
//                System.out.println("[DroneSubsystem] Received message: " + message);

                // Split the message into type and data (droneID or location update data)
                String[] parts = message.split(":", 2);
                String type = parts[0];  // This will be the message type (e.g., "JOB_DELEGATION", "LOCATION_UPDATE")
                if (parts.length >= 2) {
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
//                        System.out.println("[DroneSubsystem] Location update received. Data: " + data);
                        processDroneLocationUpdate(data); // Call your method to handle location update
                    } else if (type.equals(CommunicationDroneToSubsystem.EVENT_RETURN.name())) {
                        // Handle returned event from stuck drone
                        System.out.println("[DroneSubsystem] Received returned event from stuck drone: " + data);

                        Event returnedEvent = Event.deserialize(data);

                        // Forward the event back to the Scheduler
                        try (DatagramSocket socket = new DatagramSocket()) {
                            String eventData = returnedEvent.serialize();
                            byte[] sendData = eventData.getBytes();
                            InetAddress schedulerAddress = InetAddress.getByName("localhost");
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, 6000);
                            socket.send(sendPacket);
                            System.out.println("[DroneSubsystem -> Scheduler] Forwarded returned event: " + eventData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("[DroneSubsystem] Unknown message type: " + type);
                    }
                } else {
                    System.out.println("[DroneSubsystem] Invalid message format.");
                    System.out.println("The Parts are: "+ parts.length);
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
            synchronized (drones){
                drones.get(droneId).setCurrentPosition(new int[]{x, y});
            }
            consoleView.updateDronePosition(droneId + 1, x, y);
            consoleView.updateDroneState(droneId + 1, drones.get(droneId).getState());

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

        // Track which drone was assigned for updating nextAssignmentIndex
        Drone assignedDrone = null;

        // Find the closest idle drone, en route drone with the same severity, and returning drone
        for (int i=0; i < this.drones.size(); i++) {
            int startingIndex = (nextAssignmentIndex + i) % this.drones.size();
            Drone drone = this.drones.get(startingIndex);

            if(drone.getState() instanceof FaultedState){
                continue;
            }
            double distance = calculateDistance(drone, eventLocation);

            // Calculate the total distance the drone needs to travel (to the event and back to (0, 0))
            double distanceToEvent = calculateDistance(drone, eventLocation);
            double distanceBackToBase = calculateDistance(eventLocation, new int[]{0, 0});
            double totalDistance = distanceToEvent + distanceBackToBase;

            // Calculate the battery required for the trip
            double batteryRequired = totalDistance * drone.getBatteryDepletionRate();

            // Check if the drone has enough agent and battery
            boolean hasEnoughAgent = drone.getAgentCapacity() > 0;
            boolean hasEnoughBattery = drone.getBatteryLevel() >= batteryRequired;

            if (drone.getState() instanceof IdleState && distance < minIdleDistance && hasEnoughAgent && hasEnoughBattery) {
                minIdleDistance = distance;
                closestIdleDrone = drone;
            } else if (drone.getState() instanceof EnRouteState && isDroneEnRouteToSameSeverity(drone, event.getSeverityLevel()) && distance < minEnRouteDistance && hasEnoughAgent && hasEnoughBattery) {
                minEnRouteDistance = distance;
                closestEnRouteDrone = drone;
            } else if (drone.getState() instanceof ReturningState && distance < minReturningDistance && hasEnoughAgent && hasEnoughBattery) {
                minReturningDistance = distance;
                closestReturningDrone = drone;
            }
        }

        // 1. If a matching en route drone is found, reassign it to the new event and give its previous event to the closest idle drone
        if (closestEnRouteDrone != null && minEnRouteDistance < minIdleDistance) {
            Event originalEvent = closestEnRouteDrone.getCurrentEvent();
            event.setAssignedTime(System.currentTimeMillis() - simulationStartTime);
            if (originalEvent != null) {
                int[] originalEventLocation = zones.getZoneMidpoint(originalEvent.getZoneId());

                // Assign the original event to the closest idle drone
                if (closestIdleDrone != null) {
//                    event.setAssignedTime(System.currentTimeMillis());
                    System.out.println("IDLE DRONE FOUND ----------------------- (Drone "+ closestIdleDrone.getId()+")");
                    System.out.println("The two DRONES are switching roles now \n--------------------------------");

                    double distance = calculateDistance(closestIdleDrone, eventLocation);
                    event.setDistanceTraveled(distance);

                    // Reassign the en route drone to the new event
                    closestEnRouteDrone.setTargetPosition(eventLocation);
                    closestEnRouteDrone.setIncidentPosition(eventLocation);
                    closestEnRouteDrone.setCurrentEvent(event);

                    // Assign the original event to the closest idle drone
                    closestIdleDrone.setTargetPosition(originalEventLocation);
                    closestIdleDrone.setIncidentPosition(originalEventLocation);
                    closestIdleDrone.setCurrentEvent(originalEvent);

                    // Clear the current assignee if necessary
                    if (currentAssignee != null) {
                        currentAssignee.setCurrentEvent(null);
                    }

                    System.out.println("Drone " + closestIdleDrone.getId() + " 's new target is " + originalEventLocation[0] + "," + originalEventLocation[1] + " With Event ID: "+originalEvent.getId());
                    System.out.println("Drone " + closestEnRouteDrone.getId() + " 's new target is " + eventLocation[0] + "," + eventLocation[1]+ " With Event ID: "+event.getId());
                    System.out.println("\n\n---------------------------------");

                    // Update next assignment index to be after the idle drone that was assigned
                    assignedDrone = closestIdleDrone;
                }
            }
        }
        // 2. If no matching en route drone was found, assign the event to the closest idle drone
        else if (closestIdleDrone != null) {
            System.out.println("IDLE DRONE IS FOUND AND ABOUT TO BE ASSIGNED--------------------------(Drone "+ closestIdleDrone.getId()+")");
            event.setAssignedTime(System.currentTimeMillis() - simulationStartTime);
            double distance = calculateDistance(closestIdleDrone, eventLocation);
            event.setDistanceTraveled(distance);
            closestIdleDrone.setTargetPosition(eventLocation);
            closestIdleDrone.setIncidentPosition(eventLocation);

            Event previousEvent = closestIdleDrone.getCurrentEvent();
            if (currentAssignee != null) {
                currentAssignee.setCurrentEvent(null);
            }
            closestIdleDrone.setCurrentEvent(event);

            // Update next assignment index to be after this drone
            assignedDrone = closestIdleDrone;
        }
        // 3. If there are no idle drones, assign the event to the closest returning drone
        else if (closestReturningDrone != null && closestReturningDrone.getAgentCapacity() > 0 && minReturningDistance < minIdleDistance) {
            System.out.println("A RETURNING DRONE IS FOUND AND ABOUT TO BE ASSIGNED -> (Drone" + closestReturningDrone.getId() +")");
            event.setAssignedTime(System.currentTimeMillis() - simulationStartTime);
            closestReturningDrone.setTargetPosition(eventLocation);
            closestReturningDrone.setIncidentPosition(eventLocation);
            closestReturningDrone.setCurrentEvent(event);

            // Update next assignment index to be after this drone
            assignedDrone = closestReturningDrone;
        }
        else { // No drones available
            System.out.println("NO DRONE AVAILABLE FOR DELEGATION");
        }

        // Update nextAssignmentIndex if a drone was assigned
        if (assignedDrone != null) {
            nextAssignmentIndex = ((assignedDrone.getId()-1) % drones.size()) + 1;
            if (nextAssignmentIndex >= drones.size()) {
                nextAssignmentIndex = 0;  // Wrap around if needed
            }
            System.out.println("Next assignment will start from drone index: " + nextAssignmentIndex);
        }
    }
    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param point1 The first point as an array [x, y].
     * @param point2 The second point as an array [x, y].
     * @return The distance between the two points.
     */
    private double calculateDistance(int[] point1, int[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }

    public void updateDroneStateInConsole(int droneId, DroneState state) {
        consoleView.updateDroneState(droneId, state);
    }

    /**
     * Get a map of zones where fires are simulated
     * @return map of zones
     */
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

    /**
     * Main method to start the Drone Subsystem
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            DroneSubsystem droneSubsystem = new DroneSubsystem(6000, 6001, "docs/sample_zone_file.csv");
            System.out.println("The zones are:\n" + droneSubsystem.getZones());
            droneSubsystem.run();
        } catch (SocketException e) {
            System.err.println("Error starting DroneSubsystem: " + e.getMessage());
        }
    }
    /**
     * Returns the number of available drones that can handle the given event.
     * A drone is considered available if it is idle, has enough battery, and has enough agent capacity.
     *
     * @param event The event to be handled.
     * @return The number of available drones.
     */
    public int getAvailableDrones(Event event) {
        int availableDronesCount = 0;
        int[] eventLocation = zones.getZoneMidpoint(event.getZoneId());

        synchronized (drones) {
            for (Drone drone : drones) {
                // Check if the drone is idle
                if (drone.getCurrentEvent() == null) {
                    // Calculate the total distance the drone needs to travel (to the event and back to (0, 0))
                    double distanceToEvent = calculateDistance(drone, eventLocation);
                    double distanceBackToBase = calculateDistance(eventLocation, new int[]{0, 0});
                    double totalDistance = distanceToEvent + distanceBackToBase;

                    // Calculate the battery required for the trip
                    double batteryRequired = totalDistance * drone.getBatteryDepletionRate();

                    // Check if the drone has enough agent and battery
                    boolean hasEnoughAgent = drone.getAgentCapacity() > 0;
                    boolean hasEnoughBattery = drone.getBatteryLevel() >= batteryRequired;

                    if (hasEnoughAgent && hasEnoughBattery) {
                        availableDronesCount++;
                    }
                }
            }
        }
        return availableDronesCount;
    }

    public void updateFireZones(Event event){
        consoleView.clearFireInZone(event.getZoneId());
    }

    public DroneStatusViewer getDroneStatusViewer() {
        return droneStatusViewer;
    }

    public EventDashboard getEventDashboard() {
        return eventDashboard;
    }

    // Method to mark an event as completed
    public void markEventCompleted(Event event) {
        synchronized(drones) {
            completedEvents.add(event);
            if(completedEvents.size() == totalEventsCount) { // Check if all events are completed
                printPerformanceSummary(); // Print the summary
            }
        }
    }

    /**
     * Writes a performance summary for all completed events to a text file named "performance_report.txt"
     * The summary includes details such as response times, extinguish times, total times, and distances traveled
     * Method is called when all events are completed
     */
    public void printPerformanceSummary() {
        // Object to collect our report line by line
        StringBuilder summaryBuilder = new StringBuilder();

        // Title for the report
        summaryBuilder.append("========== ALL FIRES EXTINGUISHED ==========\n");

        // totals for events
        long totalResponseTime = 0;
        long totalExtinguishTime = 0;
        long totalSystemTime = 0;
        double totalDistanceTraveled = 0.0;

        // Loop event to gather metrics
        for (Event event : completedEvents) {
            // time from event creation to assignment
            long eventResponseTime = event.getAssignedTime() - event.getReceivedTime();

            // time from assignment to full extinguishment
            long eventExtinguishTime = event.getExtinguishedTime() - event.getAssignedTime();

            // time the event spent in the system (from creation to extinguishment)
            long eventTotalTime = event.getExtinguishedTime() - event.getReceivedTime();

            // distance the assigned drone had to travel
            double eventDistanceTraveled = event.getDistanceTraveled();

            // add to the totals
            totalResponseTime += eventResponseTime;
            totalExtinguishTime += eventExtinguishTime;
            totalSystemTime += eventTotalTime;
            totalDistanceTraveled += eventDistanceTraveled;

            // format report
            summaryBuilder.append("--------------------------------------------\n");
            summaryBuilder.append("Event ID: ").append(event.getId()).append("\n");
            summaryBuilder.append("Response Time (ms):       ").append(eventResponseTime).append("\n");
            summaryBuilder.append("Extinguish Time (ms):     ").append(eventExtinguishTime).append("\n");
            summaryBuilder.append("Total Time (ms):          ").append(eventTotalTime).append("\n");
            summaryBuilder.append("Distance Traveled:        ").append(eventDistanceTraveled).append("\n");
        }

        // total number of events
        int totalCompletedEvents = completedEvents.size();

        // format report
        summaryBuilder.append("--------------------------------------------\n");
        summaryBuilder.append("Number of events: ").append(totalCompletedEvents).append("\n");

        // compute average times
        summaryBuilder.append("Average Response Time (ms):    ")
                .append(totalCompletedEvents == 0 ? 0 : (totalResponseTime / (double) totalCompletedEvents))
                .append("\n");

        summaryBuilder.append("Average Extinguish Time (ms):  ")
                .append(totalCompletedEvents == 0 ? 0 : (totalExtinguishTime / (double) totalCompletedEvents))
                .append("\n");

        summaryBuilder.append("Average Total Time (ms):       ")
                .append(totalCompletedEvents == 0 ? 0 : (totalSystemTime / (double) totalCompletedEvents))
                .append("\n");

        // add all the distances
        summaryBuilder.append("Sum of Distances:              ").append(totalDistanceTraveled).append("\n");

        // End of report
        summaryBuilder.append("========== END OF SIMULATION METRICS ==========\n");

        // Write this text to a file
        // False cause we want to overwrite the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("performance_report.txt", false))) {
            writer.write(summaryBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the time (in milliseconds since the Unix epoch) at which this DroneSubsystem started.
     */
    public long getSimulationStartTime() {
        return this.simulationStartTime;
    }

    public List<Event> getActiveEvents() {
        return activeEvents;
    }
}

