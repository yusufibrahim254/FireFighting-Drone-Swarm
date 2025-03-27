package org.example.FireIncidentSubsystem;

import org.example.FireIncidentSubsystem.Helpers.EventReader;

import java.io.IOException;
import java.net.*;

/**
 * The FireIncident class represents the Fire Incident Subsystem, which reads fire events
 * from a file and sends them to the Scheduler via UDP for processing.
 */
public class FireIncident implements Runnable {
    private static final String EVENT_FILE = "docs/Sample_event_file.csv"; // Path to the fire event file
    private final DatagramSocket socket; // UDP socket for communication
    private final InetAddress schedulerAddress; // Address of the Scheduler
    private final int schedulerPort; // Port of the Scheduler

    /**
     * Constructs a FireIncident subsystem with the specified Scheduler host and port.
     *
     * @param schedulerHost The hostname or IP address of the Scheduler.
     * @param schedulerPort The port number of the Scheduler.
     * @throws SocketException If the UDP socket cannot be created.
     * @throws UnknownHostException If the Scheduler host is invalid.
     */
    public FireIncident(String schedulerHost, int schedulerPort) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.schedulerAddress = InetAddress.getByName(schedulerHost); // Use the IP address of the Scheduler machine
        this.schedulerPort = schedulerPort;
    }

    /**
     * Runs the FireIncident subsystem. Reads fire events from the file and sends them
     * to the Scheduler via UDP, waiting for an acknowledgment after each event.
     */
    @Override
    public void run() {
        System.out.println("[FireIncident] Listening on Port: " + this.socket.getLocalPort());
        boolean acknowledged;
        try {
            Event[] events = EventReader.readEvents(EVENT_FILE); // Read events from the file
            for (Event event : events) {
                // Serialize the event to a string
                String eventData = event.serialize();
                byte[] sendData = eventData.getBytes();

                // Send the event to the Scheduler
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                socket.send(sendPacket);
                System.out.println("[FireIncident -> Scheduler] Sent event: " + eventData);

                // Wait for acknowledgment from the Scheduler
                acknowledged = waitForAcknowledgment(event);

                Thread.sleep(10000); // events sent every 10 seconds
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close(); // Close the socket when done
        }
    }

    /**
     * Waits for an acknowledgment from the Scheduler.
     *
     * @throws IOException If an I/O error occurs while waiting for the acknowledgment.
     */
    private boolean waitForAcknowledgment(Event event) throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket); // Wait for acknowledgment
        String acknowledgment = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // Log the acknowledgment
        System.out.println("[FireIncident <- Scheduler] Received acknowledgment: " + acknowledgment+"\n\n");
        if(acknowledgment != ("ACK"+event.getId())) {
            return false;
        }
        return true;
    }

    /**
     * Main method to start the FireIncident subsystem.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
//            FireIncident fireIncident = new FireIncident("192.168.1.2", 5000); // Replace with the IP address of the Scheduler machine
            // Start the FireIncident subsystem
            FireIncident fireIncident = new FireIncident("localhost", 5000); // Scheduler is on localhost, port 5000
            fireIncident.run();
        } catch (SocketException | UnknownHostException e) {
            System.err.println("Error starting FireIncident subsystem: " + e.getMessage());
        }
    }
}