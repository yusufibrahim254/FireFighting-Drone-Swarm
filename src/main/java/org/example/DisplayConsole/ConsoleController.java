package org.example.DisplayConsole;

import org.example.DroneSystem.DroneState;
import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.Zone;
import org.example.FireIncidentSubsystem.Helpers.Zones;
import org.example.DroneSystem.DroneSubsystem;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controls the functionality of the simulation console
 */
public class ConsoleController {
    private DroneSubsystem droneSubsystem;
    private final Zones zonesModel;
    private Set<Integer> fires = new HashSet<>();
    private Set<Integer> extinguishedFires = new HashSet<>();
    private final ConcurrentHashMap<Integer, Point> dronePositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, DroneState> droneStates = new ConcurrentHashMap<>();
    private ConsoleView view;

    /**
     * Constructor for the console controller
     */
    public ConsoleController(){
        String zonesFilePath = "docs/sample_zone_file.csv";
        this.zonesModel = new Zones(zonesFilePath);
    }

    /**
     * Load the zones into a linked list
     * @return linked list of all zones
     */
    public LinkedList<Zone> getZones(){
        return zonesModel.getZoneList();
    }

    /**
     * Marks a fire in a zone on the console
     * @param zoneId the id of the zone with fire
     */
    public void markFire(int zoneId) {
        fires.add(zoneId);
        extinguishedFires.remove(zoneId);
        view.repaint();
    }


    /**
     * Clears the fire in a zone on the console (fire is extinguished)
     * @param zoneId the id of the zone with the extinguished fire
     */
    public void clearFireInZone(int zoneId) {
        fires.remove(zoneId);
        extinguishedFires.add(zoneId);
        view.repaint();
    }

    /**
     * Update the state of the drone in the simulation GUI
     * @param droneId the drone's id
     * @param state the state of the drone
     */
    public void updateDroneState(int droneId, DroneState state) {
        droneStates.put(droneId, state);
        view.repaint();
    }

    /**
     * Update the location/position of the drone in the simulation GUI
     * @param droneId the drone's id
     * @param x the x coordinates of the drone
     * @param y the y coordinates of the drone
     */
    public void updateDronePosition(int droneId, int x, int y) {
        dronePositions.put(droneId, new Point(x, y));
        view.repaint();
    }

    /**
     * Get the set of the fires currently
     * @return set of ongoing fires
     */
    public Set<Integer> getFires() {
        return fires;
    }

    /**
     * Get the set of the extinguished fires
     * @return set of extinguished fires
     */
    public Set<Integer> getExtinguishedFires() {
        return extinguishedFires;
    }

    /**
     * Get the map of all current drone locations
     * @return map of the drone locations
     */
    public Map<Integer, Point> getDronePositions() {
        return dronePositions;
    }

    /**
     * Get the map of all the current drone states
     * @return map of the drone states
     */
    public Map<Integer, DroneState> getDroneStates() {
        return droneStates;
    }

    public void setDroneSubsystem(DroneSubsystem droneSubsystem) {
        this.droneSubsystem = droneSubsystem;
    }

    public void generatePerformanceReport() {
        if (droneSubsystem != null) {
            droneSubsystem.printPerformanceSummary();
        } else {
            System.err.println("[ConsoleController] DroneSubsystem is null!");
        }
    }

    /**
     * Get the console view
     * @return the console view
     */
    public ConsoleView getView() {
        return view;
    }

    /**
     * Set the console view
     * @param view the console view
     */
    public void setView(ConsoleView view) {
        this.view = view;
    }

    /**
     * Get the size of the farthest zone to get a minimum grid size to cover all zones
     * @return minimum grid size
     */
    public int[] getZoneSize(){
        int[] farMidPoint = zonesModel.getFurthestZoneMidpoint();
        // by getting the farthest midpoint, and multiplying by 4 (2 will cover the zone edges)
        // this ensures that the grid will cover all the zones perfectly
        return new int[]{(farMidPoint[0]*4), (farMidPoint[1])*4};
    }

    public String getFireSeverity(int zoneId) {

        for (Event event : droneSubsystem.getActiveEvents()) {
            if (event.getZoneId() == zoneId) {
                return event.getSeverityLevel();
            }
        }
        return "";
    }
}
