package org.example.DisplayConsole;

import org.example.DroneSystem.DroneState;
import org.example.FireIncidentSubsystem.Helpers.Zone;
import org.example.FireIncidentSubsystem.Helpers.Zones;

import java.awt.*;
import java.util.*;

public class ConsoleController {
    private final Zones zonesModel;
    private Set<Integer> fires = new HashSet<>();
    private Set<Integer> extinguishedFires = new HashSet<>();
    private final Map<Integer, Point> dronePositions = new HashMap<>();
    private final Map<Integer, DroneState> droneStates = new HashMap<>();
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
     * @param filePath zone CSV
     * @return linked list of all zones
     */
    public LinkedList<Zone> getZones(String filePath){
        return zonesModel.loadZonesFromCSV(filePath);
    }

    public void markFire(int zoneId) {
        fires.add(zoneId);
        extinguishedFires.remove(zoneId);
        view.repaint();
    }


    public void clearFireInZone(int zoneId) {
        fires.remove(zoneId);
        extinguishedFires.add(zoneId);
        view.repaint();
    }

    public void updateDroneState(int droneId, DroneState state) {
        droneStates.put(droneId, state);
        view.repaint();
    }

    public void updateDronePosition(int droneId, int x, int y) {
        dronePositions.put(droneId, new Point(x, y));
        view.repaint();
    }

    public Set<Integer> getFires() {
        return fires;
    }

    public void setFires(Set<Integer> fires) {
        this.fires = fires;
    }


    public Set<Integer> getExtinguishedFires() {
        return extinguishedFires;
    }

    public void setExtinguishedFires(Set<Integer> extinguishedFires) {
        this.extinguishedFires = extinguishedFires;
    }

    public Map<Integer, Point> getDronePositions() {
        return dronePositions;
    }

    public Map<Integer, DroneState> getDroneStates() {
        return droneStates;
    }

    public ConsoleView getView() {
        return view;
    }

    public void setView(ConsoleView view) {
        this.view = view;
    }
}
