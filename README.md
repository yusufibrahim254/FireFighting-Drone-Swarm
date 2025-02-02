# FireFighting-Drone-Swarm

(Iteration 1)

This program simulates fire incidents as well as drones responding to them. It uses a scheduler to manage fire events reported by a fire incident subsystem and notifies the drone subsystem in order to assign drones accordingly to process the events.

## File Overview

- Main.java - Entry point to the program
- Scheduler.java - Manages the queue of incoming incidents and mainly acts as a pass through for communication between subsystems right now
- FireIncident.java - Reads fire incident events from input files and sends them to the Scheduler
- Event.java - represents fire/drone request event
- EventReader.java - helps process reading fire incident events
- DroneSubsytem.java - Manages drones and assigns them to events
- Drone.java - represents drone itself 

## Setup instructions:

1. Download submitted zip file for iteration 1
2. Extract the project and select open project with intellij (will be recognized as java project)
3. Run the main class, and observe the three threads working together
