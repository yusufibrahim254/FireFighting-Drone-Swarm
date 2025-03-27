package org.example;

import org.example.FireIncidentSubsystem.Helpers.EventType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Represents a sensor that tracks and logs specific events into a text file
 */
public class Sensor {
    private List<String> logs = Collections.synchronizedList(new ArrayList<>());
    private static final String LOG_FILE = "docs/logs/system_event_logs.txt";
    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    /**
     * Constructor for the sensor
     */
    public Sensor() {
        try (FileWriter writer = new FileWriter(LOG_FILE, false)){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduledExecutor.scheduleAtFixedRate(this::flushLogs, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Logs an event into memory
     * @param eventType the type of event
     * @param entity the entity responsible for event
     * @param details event details
     */
    private synchronized void logEvent(EventType eventType, String entity, String details){
        logs.add(String.format("[%s],%s,%s,%s", new Date(), eventType, entity, details));
    }

    /**
     * Flushes the logs from memory into a text file
     */
    private synchronized void flushLogs() {
        synchronized (logs) {
            if (logs.isEmpty()) return;
            try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
                for (String log : logs) {
                    writer.write(log + "\n");
                }
                writer.flush();
                logs.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shuts down the executor and closes the logger
     */
    public void shutdown() {
        flushLogs(); // clean out logs one last time
        scheduledExecutor.shutdown();
    }

    /**
     * Logs the event when a zone detects a fire
     * @param zoneId the id of the zone
     */
    public void detectFire(int zoneId) {
        logEvent(EventType.FIRE_DETECTED, ("Zone " + zoneId), ("Fire detected"));
    }

    /**
     * Logs the event when a zone requests a drone
     * @param zoneId the id of the zone
     */
    public void requestDrone(int zoneId) {
        logEvent(EventType.DRONE_REQUEST, ("Zone " + zoneId), ("Drone requested"));
    }

    /**
     * Logs the event when a drone arrives at a zone
     * @param droneId the id of the drone
     * @param zoneId the id of the zone
     */
    public void droneArrived(int droneId, int zoneId) {
        logEvent(EventType.DRONE_REQUEST, ("Drone " + droneId), ("Arrived at Zone " + zoneId));
    }
}
