package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The DroneSubsystem class represents the Drone Subsystem, which receives tasks from
 * the Scheduler, processes them, and sends acknowledgments back.
 */
public class DroneSubsystem implements Runnable {
    private final DatagramSocket socket; // UDP socket for communication
    private final List<Drone> drones; // List of drones managed by the subsystem

    // Default port for the DroneSubsystem
    private static final int DEFAULT_PORT = 6000;

    /**
     * Constructs a DroneSubsystem with the default port (6000).
     *
     * @throws SocketException If the UDP socket cannot be created.
     */
    public DroneSubsystem() throws SocketException {
        this(DEFAULT_PORT);
    }

    /**
     * Constructs a DroneSubsystem with the specified port.
     *
     * @param port The port number on which the Drone Subsystem listens for tasks.
     * @throws SocketException If the UDP socket cannot be created.
     */
    public DroneSubsystem(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.drones = new ArrayList<>();

        // Initialize the fleet with 10 drones
        for (int i = 1; i <= 10; i++) {
            drones.add(new Drone(i, 15));
        }

        // Start each drone in a separate thread
        for (Drone drone : drones) {
            new Thread(drone).start();
        }
    }

    /**
     * Runs the Drone Subsystem. Listens for tasks from the Scheduler, processes them,
     * and sends acknowledgments back.
     */
    @Override
    public void run() {
        System.out.println("[DroneSubsystem] Listening on Port: " + this.socket.getLocalPort());
        while (true) {
            try {
                // Receive event from Scheduler
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String eventData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("[DroneSubsystem] Received event: " + eventData);

                // Deserialize the event
                Event event = Event.deserialize(eventData);

                // Assign the event to an idle drone
                System.out.println("About to assign a drone");
                assignEventToIdleDrone(event);

                // Send acknowledgment back to Scheduler
                String ack = "ACK:" + event.getId();
                byte[] sendData = ack.getBytes();
                InetAddress schedulerAddress = receivePacket.getAddress();
                int schedulerPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
                socket.send(sendPacket);
                System.out.println("[DroneSubsystem -> Scheduler] Sent acknowledgment: " + ack + "\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assigns an event to an idle drone.
     *
     * @param event The event to be assigned.
     */
    private void assignEventToIdleDrone(Event event) {
        for (Drone drone : drones) {
            if (drone.getState() instanceof IdleState) {
                drone.assignEvent(event);
                break;
            }
        }
    }

    /**
     * Main method to start the DroneSubsystem.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Start the DroneSubsystem
            DroneSubsystem droneSubsystem = new DroneSubsystem(); // Use the default port (6000)
            droneSubsystem.run();
        } catch (SocketException e) {
            System.err.println("Error starting DroneSubsystem: " + e.getMessage());
        }
    }
}