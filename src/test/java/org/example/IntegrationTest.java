package org.example;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.example.FireIncidentSubsystem.FireIncident;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static org.junit.jupiter.api.Assertions.*;


class IntegrationTest {
    private DatagramSocket schedulerSocket;
    private DatagramSocket fireIncidentSocket;
    int schedulerPort, fireIncidentPort, droneSubsystemPort;
    private DatagramSocket droneSubsystemSocket;
    private final String host = "localhost";

    @BeforeEach
    void setUp() throws Exception {
        schedulerSocket = new DatagramSocket(0);
        schedulerPort = schedulerSocket.getLocalPort();

        fireIncidentSocket = new DatagramSocket(0);
        fireIncidentPort = fireIncidentSocket.getLocalPort();

        droneSubsystemSocket = new DatagramSocket(0);
        droneSubsystemPort = droneSubsystemSocket.getLocalPort();
    }

    @AfterEach
    void tearDown() {
        schedulerSocket.close();
        fireIncidentSocket.close();
        droneSubsystemSocket.close();
    }

    @Test
    void testPacketCommunication() throws Exception {
        // thread to act as scheduler
        Thread schedulerThread = new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                schedulerSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                String responseMessage = "ACK: " + receivedMessage;
                byte[] sendData = responseMessage.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                schedulerSocket.send(sendPacket);
            } catch (Exception e) {
                if (!schedulerSocket.isClosed()) e.printStackTrace();
            }
        });

        Thread droneThread = new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                droneSubsystemSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                String responseMessage = "ACK: " + receivedMessage;
                byte[] sendData = responseMessage.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                droneSubsystemSocket.send(sendPacket);
            } catch (Exception e) {
                if (!droneSubsystemSocket.isClosed()) e.printStackTrace();
            }
        });

        schedulerThread.start();
        droneThread.start();

        // FireIncident client
        FireIncident fireIncident = new FireIncident(host, schedulerPort);
        new Thread(fireIncident).start();

        // Send event from scheduler to drone
        Scheduler scheduler = new Scheduler(0, host, droneSubsystemPort);
        boolean result = scheduler.sendEventToDrone(new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT"));
        assertTrue(result);

        schedulerThread.join();
        droneThread.join();
    }
}