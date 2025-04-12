package org.example.FireIncidentSubsystem;

import org.example.DisplayConsole.OperatorView;
import org.example.FireIncidentSubsystem.Helpers.EventReader;

import java.io.IOException;
import java.net.*;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The FireIncident class represents the Fire Incident Subsystem, which reads fire events
 * from a file and sends them to the Scheduler via UDP for processing.
 */
public class FireIncident implements Runnable {
    private static final String EVENT_FILE = "docs/Sample_event_file.csv"; // Path to the fire event file
    private final DatagramSocket socket; // UDP socket for communication
    private final InetAddress schedulerAddress; // Address of the Scheduler
    private final int schedulerPort; // Port of the Scheduler
    private OperatorView view;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final double simulationSpeed = 2;


    /**
     * Constructs a FireIncident subsystem with the specified Scheduler host and port.
     *
     * @param schedulerHost The hostname or IP address of the Scheduler.
     * @param schedulerPort The port number of the Scheduler.
     * @throws SocketException      If the UDP socket cannot be created.
     * @throws UnknownHostException If the Scheduler host is invalid.
     */
    public FireIncident(String schedulerHost, int schedulerPort) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.schedulerAddress = InetAddress.getByName(schedulerHost); // Use the IP address of the Scheduler machine
        this.schedulerPort = schedulerPort;
        this.view = new OperatorView(this);
    }

    /**
     * Runs the FireIncident subsystem. Reads fire events from the file and sends them
     * to the Scheduler via UDP, waiting for an acknowledgment after each event.
     */
    @Override
    public void run() {
        System.out.println("[FireIncident] Listening on Port: " + this.socket.getLocalPort());
        System.out.println("[FireIncident] Simulation speed: " + simulationSpeed + "x");

        try {
            Event[] events = EventReader.readEvents(EVENT_FILE); // Read events from the file

            if (events.length == 0) {
                System.out.println("No events found in the file.");
                return;
            }

            // Send first event immediately
            sendEvent(events[0]);

            // Process remaining events with time delays
            for (int i = 1; i < events.length; i++) {
                long timeDifference = calculateTimeDifference(events[i-1].getTime(), events[i].getTime());
                long scaledDelay = (long)(timeDifference / simulationSpeed);

                if (scaledDelay > 0) {
                    System.out.println("Waiting " + (scaledDelay / 1000) + " seconds before next event...");
                    Thread.sleep(scaledDelay);
                }

                sendEvent(events[i]);
            }
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        } finally {
            socket.close(); // Close the socket when done
        }
    }
    /**
     * Sends an event to the scheduler and waits for acknowledgment
     */
    private void sendEvent(Event event) throws IOException {
        String eventData = event.serialize();
        byte[] sendData = eventData.getBytes();

        // Send the event to the Scheduler
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
        socket.send(sendPacket);
        System.out.println("[FireIncident -> Scheduler] Sent event: " + eventData);

        // Wait for acknowledgment from the Scheduler
        waitForAcknowledgment(event);
    }
    /**
     * Calculates the time difference in milliseconds between two time strings
     */
    private long calculateTimeDifference(String time1, String time2) throws ParseException {
        Date date1 = TIME_FORMAT.parse(time1);
        Date date2 = TIME_FORMAT.parse(time2);
        return date2.getTime() - date1.getTime();
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
        System.out.println("[FireIncident <- Scheduler] Received acknowledgment: " + acknowledgment + "\n\n");
        return acknowledgment.equals("ACK" + event.getId());
    }

    /**
     * Gets the operator view
     * @return the operator view
     */
    public OperatorView getView() {
        return view;
    }

    /**
     * Sends event to scheduler manually instead of polling through a list of events
     * @param event the event to send
     * @throws IOException if i/o error occurs
     */
    public void manualSendEvent(Event event) throws IOException {
        String eventData = event.serialize();
        byte[] sendData = eventData.getBytes();

        // Send the event to the Scheduler
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
        socket.send(sendPacket);
        System.out.println("[FireIncident -> Scheduler] Sent event from Operator: " + eventData);

        // Wait for acknowledgment from the Scheduler
        waitForAcknowledgment(event);
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