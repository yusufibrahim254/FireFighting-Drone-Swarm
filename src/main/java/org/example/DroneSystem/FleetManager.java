//package org.example.DroneSystem;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Manager of the fleet of drones, handles responsibilities such as
// * adding drones, checking availability of specific drone, and
// * maintaining fleet health.
// */
//public class FleetManager {
//    private final List<Drone> drones; // List of drones in the fleet
//
//    /**
//     * Constructs a FleetManager and initializes the fleet with drones.
//     *
//     * @param droneSubsystemHost The hostname or IP address of the DroneSubsystem.
//     * @param droneSubsystemPort The port number of the DroneSubsystem.
//     */
//    public FleetManager(String droneSubsystemHost, int droneSubsystemPort) {
//        this.drones = new ArrayList<>();
//
//        // Initialize the fleet with 10 drones
//        for (int i = 1; i <= 2; i++) {
//            try {
//                drones.add(new Drone(i, 15, droneSubsystemHost, droneSubsystemPort));
//            } catch (IOException e) {
//                System.err.println("Error creating Drone " + i + ": " + e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * Gets the list of drones in the fleet.
//     *
//     * @return The list of drones.
//     */
//    public List<Drone> getDrones() {
//        return drones;
//    }
//}