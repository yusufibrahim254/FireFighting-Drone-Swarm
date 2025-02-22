# FireFighting-Drone-Swarm

(Iteration 2)

This program simulates fire incidents as well as drones responding to them. It uses a scheduler to manage fire events reported by a fire incident subsystem and notifies the drone subsystem in order to assign drones accordingly to process the events.

## File Overview
- Main.java - Entry point to the program
- Scheduler.java - Manages the queue of incoming incidents and mainly acts as a pass through for communication between subsystems right now
- Drone System
  - BayController.java - Controls the bay doors of the drone
  - Drone.java - Represents drone itself
  - DroneEvent.java - Handles the drone's event
  - DroneState.java - Interface represents the various states a firefighting drone can be in
  - DroneSubsystem.java - Interacts with the Scheduler to assign drones to fire incidents
  - FleetManager.java - Manages and coordinates the fleet of drones
 
- Fire Incident Subsystem
  - FireIncident.java - Reads fire incident events from input files and sends them to the Scheduler
  - Event.java - Represents fire/drone request event
  - EventReader.java - Helps process reading fire incident events
  - Severity.java - Helps manage Severity levels for fire incidents
  - Zone.java - Represents the zone in which the fire incident is taking place
  - ZoneReader.java - Helps process reading zone locations
  - Coordinates.java - Helps manage coordinates of fire incident zones

## Setup instructions:

1. Download submitted zip file for iteration 1
2. Extract the project and select open project with intellij (will be recognized as java project)
3. If prompted, click `Import Maven Projects`. If not, right-click the pom.xml file and select `Add as Maven Project`
4. Go to `View > Tool Windows > Maven` to open the Maven pane
5. In the Maven panel, click the Refresh button (a circle arrow icon) to download dependencies
6. Click on the Build menu at the top and select Build Project or press Ctrl+F9
7. Run the main class, and observe the three threads working together

## Contact
[Abdel Qayyim Maazou Yahaya](mailto:ABDELQAYYIMYAHAYA@cmail.carleton.ca) <br>
[Andrei Chirilov](mailto:ANDREICHIRILOV@cmail.carleton.ca) <br>
[Hundey Kuma](mailto:HUNDEYKUMA@cmail.carleton.ca) <br>
[Mahad Ahmed](mailto:MAHADAHMED3@cmail.carleton.ca) <br>
[Yasmin Hersi Adawe](mailto:YASMINHERSIADAWE@cmail.carleton.ca) <br>
[Yusuf Ibrahim](mailto:YUSUFIBRAHIM3@cmail.carleton.ca) <br>
