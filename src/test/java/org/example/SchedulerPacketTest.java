package org.example;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for testing the packet communication of the Scheduler class with the drone.
 * This class tests both valid and faulty event scenarios using the scheduler's packet sending functionality.
 */
class SchedulerPacketTest {
    private Scheduler scheduler;
    private Event event, faultyEvent;
    private DatagramSocket serverSocket;
    private int serverPort;
    private final String serverHost = "localhost";

    /**
     * Sets up the test environment, initializing necessary components like sockets and events.
     * This method runs before each test.
     */
    @BeforeEach
    void setUp() throws Exception {
        serverSocket = new DatagramSocket(0);
        serverPort = serverSocket.getLocalPort();
        scheduler = new Scheduler(0, serverHost, serverPort);
        event = new Event(1, "12:12:12", 2, EventType.DRONE_REQUEST, "High", "NO_FAULT");
        faultyEvent = new Event(1, "09:09:09", 2, EventType.DRONE_REQUEST, "Low", "CORRUPTED_MESSAGE");
    }

    /**
     * Cleans up resources after each test, closing the server socket.
     * This method runs after each test.
     */
    @AfterEach
    void tearDown() {
        serverSocket.close();
    }

    /**
     * Test case to verify sending a valid event to the drone.
     * It tests whether the Scheduler sends the event correctly and receives an acknowledgment.
     */
    @Test
    void testSendEventToDrone() throws Exception {
        // Server thread to receive packets and send acknowledgment
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);  // Receive data from client (scheduler)
                DatagramPacket sendPacket = getDatagramPacket(receivePacket);
                serverSocket.send(sendPacket);  // Send acknowledgment back to client
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Sending event to drone
        boolean result = scheduler.sendEventToDrone(event);
        assertTrue(result);  // Ensure event is successfully sent
    }

    /**
     * Helper method to create a DatagramPacket with acknowledgment message.
     * It is used by the server to respond back to the client (scheduler).
     *
     * @param receivePacket The packet received from the client.
     * @return A new DatagramPacket with the acknowledgment message.
     */
    private static DatagramPacket getDatagramPacket(DatagramPacket receivePacket) {
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // Create acknowledgment message
        String responseMessage = "ACK: " + receivedMessage;
        byte[] sendData = responseMessage.getBytes();
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        return new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
    }

    /**
     * Test case to verify the handling of a corrupted message.
     * It tests whether the scheduler correctly handles faulty events.
     */
    @Test
    void testCorruptMessage() throws Exception {
        // Server thread to receive packets and send acknowledgment
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);  // Receive data from client (scheduler)
                DatagramPacket sendPacket = getDatagramPacket(receivePacket);
                serverSocket.send(sendPacket);  // Send acknowledgment back to client
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Sending faulty event to drone
        boolean result = scheduler.sendEventToDrone(faultyEvent);
        assertFalse(result);  // Ensure that sending a corrupted event fails
    }
}
