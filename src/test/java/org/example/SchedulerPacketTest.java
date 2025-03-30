// Inside `src/test/java/org/example/SchedulerTest.java`
package org.example;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static org.junit.jupiter.api.Assertions.*;


class SchedulerPacketTest {
    private Scheduler scheduler;
    private Event event, faultyEvent;
    private DatagramSocket serverSocket;
    private final int serverPort = 6000;
    private final String serverHost = "localhost";

    @BeforeEach
    void setUp() throws Exception {
        serverSocket = new DatagramSocket(serverPort);
        scheduler = new Scheduler(5000, serverHost, serverPort);
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT");
        faultyEvent = new Event(1, "09:09:09", 2, EventType.DRONE_REQUEST, "Low", "CORRUPT_MESSAGE");
    }

    @Test
    void testSendEventToDrone() throws Exception {
        // Server thread to receive packets
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                DatagramPacket sendPacket = getDatagramPacket(receivePacket);
                serverSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // sending event to drone
        boolean result = scheduler.sendEventToDrone(event);
        assertTrue(result);
    }

    private static DatagramPacket getDatagramPacket(DatagramPacket receivePacket) {
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // response back to client
        String responseMessage = "ACK: " + receivedMessage;
        byte[] sendData = responseMessage.getBytes();
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        return new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
    }

    @Test
    void testCorruptMessage() throws Exception {
        // Server thread to receive packets
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                DatagramPacket sendPacket = getDatagramPacket(receivePacket);
                serverSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // sending faulty event to drone
        boolean result = scheduler.sendEventToDrone(faultyEvent);
        assertFalse(result);
    }
}