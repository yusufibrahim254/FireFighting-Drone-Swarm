# FireFighting-Drone-Swarm

(Iteration 5)

This program simulates fire incidents as well as drones responding to them. It uses a scheduler to manage fire events reported by a fire incident subsystem and notifies the drone subsystem in order to assign drones accordingly to process the events. Fault detection and handling mechanisms were added to simulate and manage various fault scenarios that can occur during the drone operation.

A graphical user interface (GUI) was implemented to showcase a live simulation of all the ongoing fire events in each respective zone and the live location of the firefighting drones. The GUI monitors each drone's location, state, battery life and tank; as well as each ongoing event and the amount of agent needed to extinguish the event. Drones on the GUI are colour-coded based on their state.

## File Overview
- Main.java - Not used anymore (FireIncident, Scheduler and DroneSubsystem each have their own main class as they use UDP to communicate)
- FireIncident.java
  - Reads the incident csv file, create an event object for all the events and adds them to an events array
  - Send all the events to the schedule using UDP, everytime an event is sent, it waits for an acknowledgement from the scheduler before it sends the next one

- Fire Incident Subsystem
  - FireIncident.java - Reads fire incident events from input files and sends them to the Scheduler
  - Event.java - Represents fire/drone request event
  - EventReader.java - Helps process reading fire incident events
  - Severity.java - Helps manage Severity levels for fire incidents
  - Zone.java - Represents the zone in which the fire incident is taking place
  - ZoneReader.java - Helps process reading zone locations
  - Coordinates.java - Helps manage coordinates of fire incident zones

- Scheduler.java 
  - Receives events from FireIncident class through UDP
  - Sends acknowledgement/negative acknowledgment to FireIncident if the event is valid or faulted
  - Adds the event to a queue
  - Sends the event to the DroneSubsystem and waits for response
  - If response is ACK: NO, it means there are no drones currently available
  - It will keep the event in the queue and try again every 2 seconds
  - If the event is successfully assigned to a drone, then it removes it from the queue and sends the next event in the queue

- Drone System
  - BayController.java - Controls the bay doors of the drone
  - Drone.java - Represents drone itself
  - DroneEvent.java - Handles the drone's event
  - DroneState.java - Interface represents the various states a firefighting drone can be in
  - DroneSubsystem.java - Interacts with the Scheduler to assign drones to fire incidents

- DroneSubsystem.java
    - Receives events from Schedule class through UDP
    - Attempts to assign a drone to that event
    - The criteria for assigning an event to a drone are the following
    - 1 - Ge the closest drone to that event's location
    - 2 - En route drone with an event of the same severity is first
    - 3 - Then drones that are returning, have enough battery and agent are second
    - 4 - Idle drone are then next
    - If there are no drones available ACK: NO is sent back to the scheduler, meaning no drones are available for the task

- Drone.java
    - Receives an event from the DroneSubsystem
    - Travels to the event's location
    - Drops agent
    - Returns to original location to refuel (agent and batter)
    - If the fire is not put out, it will try to delegate the event to an available drone
    - Once it has refueled, it goes into the IDLE state
- Logger.java - Handles the logging and metrics functionality
- Display Console
  - ConsoleController.java - Handles logic and updates for the simulation display
  - ConsoleView.java - Draws the grid, zones, fires, and drone movements
  - DroneStatusViewer.java - Shows live drone battery and agent levels
  - EventDashboard.java - Displays active events and fire statuses
  - Home.java - Main window that combines all display components
  - Legend.java - Shows the meaning of icons and colors used in the display
  - OperatorController.java - Manages user interactions in the admin view
  - OperatorView.java - Admin panel where users can trigger and configure event

## Data Files
Below are the files containing the necessary data for the functionality of the simulation
- Sample_event_file.csv - Contains all the fire events taking place in the system
- sample_zone_file.csv - Contains all the zones in the system
- timings.csv - Collects the timings of the system
- performance_report.txt - Collects the performance metrics from the logging subsystem

## Setup instructions:

1. Download submitted zip file for iteration 5
2. Extract the project and select open project with intellij (will be recognized as java project)
3. If prompted, click `Import Maven Projects`. If not, right-click the pom.xml file and select `Add as Maven Project`
4. Go to `View > Tool Windows > Maven` to open the Maven pane
5. In the Maven panel, click the Refresh button (a circle arrow icon) to download dependencies
6. Click on the Build menu at the top and select Build Project or press Ctrl+F9
7. Run the Scheduler class first, then the DroneSubSystem class and finally the FireIncident class and observe the output.


## Contact
[Abdel Qayyim Maazou Yahaya](mailto:ABDELQAYYIMYAHAYA@cmail.carleton.ca) <br>
[Andrei Chirilov](mailto:ANDREICHIRILOV@cmail.carleton.ca) <br>
[Hundey Kuma](mailto:HUNDEYKUMA@cmail.carleton.ca) <br>
[Mahad Ahmed](mailto:MAHADAHMED3@cmail.carleton.ca) <br>
[Yasmin Hersi Adawe](mailto:YASMINHERSIADAWE@cmail.carleton.ca) <br>
[Yusuf Ibrahim](mailto:YUSUFIBRAHIM3@cmail.carleton.ca) <br>
