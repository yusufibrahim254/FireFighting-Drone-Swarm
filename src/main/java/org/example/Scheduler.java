package org.example;

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

        // Print a message to let the user know the Scheduler is running
        System.out.println("[Scheduler] Listening on Port: " + this.socket.getLocalPort());
    }

    /**
     * Runs the Scheduler. Listens for fire incidents, adds them to the queue, and
     * assigns drones to handle them.
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
                    // Handle acknowledgment (from DroneSubsystem)
                    System.out.println("[Scheduler <- DroneSubsystem] Received acknowledgment: " + message);
                } else {
                    // Handle event (from FireIncident)
                    System.out.println("[Scheduler <- FireIncident] Received event: " + message);

                    // Deserialize the event and add it to the queue
                    Event event = Event.deserialize(message);
                    incidentQueue.add(event);

                    // Assign a drone to the event
                    assignDroneToEvent(event);

                    // Wait for acknowledgment from the DroneSubsystem
                    waitForAcknowledgment();

                    // Send acknowledgment back to FireIncident
                    String ack = "ACK:" + event.getId();
                    byte[] sendData = ack.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    socket.send(sendPacket);
                    System.out.println("[Scheduler -> FireIncident] Sent acknowledgment: " + ack+"\n\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assigns a drone to a fire incident by sending the event to the Drone Subsystem.
     *
     * @param event The fire incident to be assigned.
     * @throws IOException If the event cannot be sent to the Drone Subsystem.
     */
    private void assignDroneToEvent(Event event) throws IOException {
        String eventData = event.serialize(); // Serialize the event to a string
        byte[] sendData = eventData.getBytes();
        InetAddress droneAddress = InetAddress.getByName(droneHost);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, droneAddress, dronePort);
        socket.send(sendPacket); // Send the event to the Drone Subsystem
        System.out.println("[Scheduler -> DroneSubsystem] Sent event: " + eventData);
    }

    /**
     * Waits for an acknowledgment from the DroneSubsystem.
     *
     * @throws IOException If an I/O error occurs while waiting for the acknowledgment.
     */
    private void waitForAcknowledgment() throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String acknowledgment = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // Ensure the received message is an acknowledgment
        if (acknowledgment.startsWith("ACK:")) {
            System.out.println("[Scheduler <- DroneSubsystem] Received acknowledgment: " + acknowledgment);
        } else {
            // If it's not an acknowledgment, log it as an unexpected message
            System.out.println("[Scheduler] Received unexpected message: " + acknowledgment);
        }
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