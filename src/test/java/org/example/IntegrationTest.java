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
    private DatagramSocket droneSubsystemSocket;
    private final int schedulerPort = 4567;
    private final int fireIncidentPort = 9832;
    private final int droneSubsystemPort = 7004;
    private final String host = "localhost";

    @BeforeEach
    void setUp() throws Exception {
        schedulerSocket = new DatagramSocket(schedulerPort);
        fireIncidentSocket = new DatagramSocket(fireIncidentPort);
        droneSubsystemSocket = new DatagramSocket(droneSubsystemPort);
    }

    @AfterEach
    void tearDown(){
        schedulerSocket.close();
        fireIncidentSocket.close();
        droneSubsystemSocket.close();
    }

    @Test
    void testPacketCommunication() throws Exception {
        // thread to act as scheduler
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                schedulerSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // send back a response to the FireIncident
                String responseMessage = "ACK: " + receivedMessage;
                byte[] sendData = responseMessage.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                schedulerSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        //Thread to act as DroneSubsystem
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                droneSubsystemSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // send back a response to the Scheduler
                String responseMessage = "ACK: " + receivedMessage;
                byte[] sendData = responseMessage.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                droneSubsystemSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Test sending a packet from FireIncident to Scheduler
        FireIncident fireIncident = new FireIncident(host, schedulerPort);
        new Thread(fireIncident).start();

        // Test sending a packet from Scheduler to DroneSubsystem
        Scheduler scheduler = new Scheduler(5000, host, droneSubsystemPort);
        boolean result = scheduler.sendEventToDrone(new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High"));
        assertTrue(result);
    }
}