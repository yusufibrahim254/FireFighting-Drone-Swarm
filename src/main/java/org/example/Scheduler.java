package org.example;

import org.example.DisplayConsole.ConsoleView;
import org.example.DisplayConsole.Home;
import org.example.FireIncidentSubsystem.Event;

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.LinkedList;

/**
 * The Scheduler class coordinates fire incidents and assigns drones to handle them.
 * It receives fire events from the FireIncident Subsystem via UDP and forwards them
 * to the Drone Subsystem for processing.
 */
public class Scheduler implements Runnable {
    private final DatagramSocket socket; // UDP socket for communication
    private final Queue<Event> incidentQueue = new LinkedList<>(); // Queue to store incoming fire incidents
    private final String droneHost; // Hostname or IP address of the Drone Subsystem
    private final int dronePort; // Port number of the Drone Subsystem
    private ConsoleView consoleView;

    /**
     * Constructs a Scheduler with the specified port, Drone Subsystem host, and port.
     *
     * @param port The port number on which the Scheduler listens for fire incidents.
     * @param droneHost The hostname or IP address of the Drone Subsystem.
     * @param dronePort The port number of the Drone Subsystem.
     * @throws SocketException If the UDP socket cannot be created.
     */
    public Scheduler(int port, String droneHost, int dronePort) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.droneHost = droneHost; // Use the IP address of the DroneSubsystem machine
        this.dronePort = dronePort;
        Home home = new Home();
        this.consoleView = home.getView();

        // Print a message to let the user know the Scheduler is running
        System.out.println("[Scheduler] Listening on Port: " + this.socket.getLocalPort());
        // Start a background thread to continuously send events to the DroneSubsystem
        new Thread(this::processIncidentQueue).start();
    }

    /**
     * Runs the Scheduler. Listens for fire incidents, adds them to the queue.
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Receive a packet
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Check if the message is an acknowledgment
                if (message.startsWith("ACK:")) {
                    System.out.println("[Scheduler <- DroneSubsystem] Received acknowledgment: " + message);
                } else {
                    // Handle event (from FireIncident)
                    System.out.println("\n[Scheduler <- FireIncident] Received event: " + message);

                    // Deserialize the event and add it to the queue
                    Event event = Event.deserialize(message);
                    if (event.isValidEvent(event)) {
                        synchronized (incidentQueue) {
                            incidentQueue.add(event);
                        }


                        consoleView.markFire(event.getZoneId());

                        // Send acknowledgment back to FireIncident
                        String ack = "ACK:" + event.getId();
                        byte[] sendData = ack.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                        socket.send(sendPacket);
                        System.out.println("[Scheduler -> FireIncident] Sent acknowledgment: " + ack + "\n\n");
                    } else {// invalid event (discarded the event)
                        String errorMessage = "NAK:" + event.getId();
                        byte[] sendErrorData = errorMessage.getBytes();
                        DatagramPacket sendErrorPacket = new DatagramPacket(sendErrorData, sendErrorData.length, receivePacket.getAddress(), receivePacket.getPort());
                        socket.send(sendErrorPacket);
                        System.out.println("[Scheduler -> FireIncident] Sent back invalid event: " + event.getId() + "\n\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Continuously attempts to send events to the DroneSubsystem.
     */
    private void processIncidentQueue() {
        while (true) {

            try {
                Event event;
                synchronized (incidentQueue) {
                    event = incidentQueue.peek(); // Get the event without removing it
                }
                if (event != null) {
                    boolean assigned = sendEventToDrone(event);
                    if (assigned) {
                        synchronized (incidentQueue) {
                            incidentQueue.poll(); // Remove only if successfully assigned
                        }
                        System.out.println("Sending event to DroneSubsystem");
                    } else {
                            System.out.println("[Scheduler] Retrying corrupted event: " + event.getId());
                    }
                }
                // Avoid tight looping
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    /**
     * Sends an event to the DroneSubsystem and waits for acknowledgment.
     *
     * @param event The fire incident to be assigned.
     * @return true if successfully assigned, false if no drone was available.
     */
    protected boolean sendEventToDrone(Event event) {
        try (DatagramSocket ackSocket = new DatagramSocket()) { // Separate socket for acknowledgments
            String eventData = event.serialize();

            if ("CORRUPTED_MESSAGE".equals(event.getFault())) {
                eventData = corruptData(eventData);
            }

            if (!checkIfValidMessage(eventData)) {
                System.out.println("[Scheduler] Corrupted message detected. Clearing to prepare retry.");
                event.setFault("NO_FAULT");
                return false;
            }

            byte[] sendData = eventData.getBytes();
            InetAddress droneAddress = InetAddress.getByName(droneHost);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, droneAddress, dronePort);

            // Send event using ackSocket to ensure the acknowledgment comes back to the right socket
            ackSocket.send(sendPacket);
            System.out.println("[Scheduler -> DroneSubsystem] Sent event: " + eventData);

            // Wait for acknowledgment using the new socket
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            ackSocket.receive(receivePacket);  // Waiting for acknowledgment on the new socket
            String acknowledgment = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            System.out.println("[Scheduler <- DroneSubsystem] Received acknowledgment: " + acknowledgment);

            if ("ACK: NO".equalsIgnoreCase(acknowledgment)) {
                System.out.println("[Scheduler] No available drones. Will retry later...");
                return false;
            }

            if (acknowledgment.startsWith("ACK:")) {
                System.out.println("[Scheduler] Drone assigned successfully.");
                return true;
            }

            System.out.println("[Scheduler] Unexpected response from DroneSubsystem: " + acknowledgment);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String corruptData(String data) {
        //Simulate corruption by modifying data in some unexpected way
        return data.substring(0, Math.max(1, data.length() / 2)) + "#$%";
    }

    private boolean checkIfValidMessage(String data) {
        // validity test to see if it doesnt have that corruption
        return !data.contains("#$%");
    }
    /**
     * Main method to start the Scheduler.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
//            Scheduler scheduler = new Scheduler(5000, "192.168.1.3", 6000); // Replace with the IP address of the DroneSubsystem machine
            // Start the Scheduler
            Scheduler scheduler = new Scheduler(5000, "localhost", 6000); // Listen on port 5000, DroneSubsystem on localhost, port 6000
            scheduler.run();
        } catch (SocketException e) {
            System.err.println("Error starting Scheduler: " + e.getMessage());
        }
    }
}