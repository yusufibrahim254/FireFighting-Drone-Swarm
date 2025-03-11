# FireFighting-Drone-Swarm

(Iteration 3)

This program simulates fire incidents as well as drones responding to them. It uses a scheduler to manage fire events reported by a fire incident subsystem and notifies the drone subsystem in order to assign drones accordingly to process the events.

# Iteration-3 Group Task Assignment
 - UML Class Diagram
 - UML State Diagram
 - UML Sequence Diagram
 - FireIncident changed to use UDP to communicate with scheduler
 - Scheduler changed to use UDP to communicate to both FireIncident and DroneSubsytem
 - DroneSubsystem changed to use UDP to communicate to schedule and assign drones
 - Handle Drone delegation for events

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


## Sequence Diagram PlantUML code
@startuml
actor User
participant "FireIncident" as FI
participant "Scheduler" as S
participant "DroneSubsystem" as DS
participant "Drone" as D

User -> FI: Start FireIncident
activate FI

FI -> FI: Read events from file
FI -> S: Send event (UDP)
activate S
S -> FI: Acknowledgment (ACK)
deactivate S

FI -> S: Send next event (UDP)
activate S
S -> FI: Acknowledgment (ACK)
deactivate S

FI -> FI: Continue sending events...

S -> DS: Forward event (UDP)
activate DS
DS -> DS: Check for available drones
DS -> S: Acknowledgment (ACK)
deactivate DS

S -> DS: Forward next event (UDP)
activate DS
DS -> DS: Assign event to closest idle drone
DS -> D: Assign task to Drone
activate D

D -> DS: Location update (UDP)
deactivate D
DS -> S: Acknowledgment (ACK)
deactivate DS

S -> FI: Acknowledgment (ACK)
deactivate S

D -> D: Move towards target
D -> D: Check if target reached
D -> D: Transition to DroppingAgentState
D -> D: Drop agent
D -> D: Transition to ReturningState
D -> D: Move towards base
D -> D: Check if base reached
D -> D: Transition to IdleState

D -> DS: Location update (UDP)
activate DS
DS -> DS: Update drone position
deactivate DS

FI -> FI: Close socket and terminate
deactivate FI

@enduml


# PlantUML Class Diagram
@startuml
package org.example.FireIncidentSubsystem {
class FireIncident {
- EVENT_FILE: String
- socket: DatagramSocket
- schedulerAddress: InetAddress
- schedulerPort: int
+ FireIncident(String schedulerHost, int schedulerPort)
+ run(): void
- waitForAcknowledgment(): void
+ main(String[] args): void
}

    class Event {
        - id: int
        - time: String
        - zoneId: int
        - eventType: EventType
        - severityLevel: String
        - assignedDrone: Drone
        - currentWaterAmountNeeded: double
        + Event(int id, String time, int zoneId, EventType eventType, String severityLevel)
        + getId(): int
        + getTime(): String
        + getZoneId(): int
        + getEventType(): EventType
        + getSeverityLevel(): String
        + getSeverityWaterAmount(): int
        + serialize(): String
        + deserialize(String data): Event
        + toString(): String
        + getCurrentWaterAmountNeeded(): double
        + setCurrentWaterAmountNeeded(double currentWaterAmountNeeded): void
        + getAssignedDrone(): Drone
        + setAssignedDrone(Drone assignedDrone): void
    }

    class EventReader {
        + EventReader()
        + readEvents(String fileName): Event[]
    }

    class Zones {
        - zoneMap: Map<Integer, int[]>
        + Zones(String filePath)
        - loadZonesFromCSV(String filePath): void
        + getZoneMidpoint(int zoneId): int[]
        + isZoneValid(int zoneId): boolean
        + toString(): String
        + getFurthestZoneMidpoint(): int[]
    }

    class Severity {
        - level: String
        - waterAmount: int
        - LOW_SEVERITY: int
        - MODERATE_SEVERITY: int
        - HIGH_SEVERITY: int
        + Severity(String level)
        - setWaterAmount(String level): void
        + getLevel(): String
        + getWaterAmount(): int
    }

    class Coordinates {
        - x: int
        - y: int
        + Coordinates(int x, int y)
        + getX(): int
        + getY(): int
        + toString(): String
        + equals(Object o): boolean
    }

    enum EventType {
        FIRE_DETECTED
        DRONE_REQUEST
    }
}

package org.example.DroneSystem {
class DroneSubsystem {
- schedulerSocket: DatagramSocket
- droneSocket: DatagramSocket
- drones: List<Drone>
- zones: Zones
+ DroneSubsystem(int schedulerPort, int dronePort, String zonesFilePath)
+ run(): void
- handleSchedulerMessages(): void
- handleDroneLocationUpdates(): void
- processDroneLocationUpdate(String locationData): void
- assignEventToClosestIdleDrone(Event event): void
- calculateDistance(int[] point1, int[] point2): double
+ getZones(): Zones
- isDroneEnRouteToSameSeverity(Drone drone, String severity): boolean
- calculateDistance(Drone drone, int[] location): double
+ main(String[] args): void
+ getAvailableDrones(Event event): int
}

    class Drone {
        - id: int
        - battery: double
        - agentCapacity: double
        - maxAgentCapacity: double
        - state: DroneState
        - bayController: BayController
        - remainingWaterNeeded: double
        - currentPosition: int[]
        - incidentPosition: int[]
        - targetPosition: int[]
        - lastSentPosition: int[]
        - currentEvent: Event
        - batteryDepletionRate: double
        + Drone(int id, double initialCapacity, DroneSubsystem droneSubsystem, double batteryDepletionRate)
        + setCurrentPosition(int[] position): void
        + delegateJob(): void
        + run(): void
        - hasTargetChanged(): boolean
        + moveTowardsTarget(): void
        - sendPositionToSubsystem(): void
        - hasReachedTarget(): boolean
        + getMaxAgentCapacity(): double
        + getAgentCapacity(): double
        + setAgentCapacity(double agentCapacity): void
        + getBayController(): BayController
        + getRemainingWaterNeeded(): double
        + setRemainingWaterNeeded(double remainingWaterNeeded): void
        + getBatteryLevel(): double
        + setBatteryLevel(int newLevel): void
        + getState(): DroneState
        + setState(DroneState state): void
        + setCurrentEvent(Event currentEvent): void
        + getCurrentEvent(): Event
        + getIncidentPosition(): int[]
        + setIncidentPosition(int[] incidentPosition): void
        + getBatteryDepletionRate(): double
        + getId(): int
        + getCurrentPosition(): int[]
        + setTargetPosition(int[] targetPosition): void
    }

    class BayController {
        - bayDoorOpen: boolean
        + BayController()
        + openBayDoors(): void
        + closeBayDoors(): void
        + isBayDoorOpen(): boolean
    }

    interface DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class IdleState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class EnRouteState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class DroppingAgentState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class RefillingState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class FaultedState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    class ReturningState implements DroneState {
        + dispatch(Drone drone): void
        + arrive(Drone drone): void
        + dropAgent(Drone drone): double
        + refill(Drone drone): void
        + fault(Drone drone): void
        + reset(Drone drone): void
        + displayState(Drone drone): void
    }

    enum CommunicationDroneToSubsystem {
        LOCATION_UPDATE
        JOB_DELEGATION
    }
}

package org.example {
class Scheduler {
- socket: DatagramSocket
- incidentQueue: Queue<Event>
- droneHost: String
- dronePort: int
+ Scheduler(int port, String droneHost, int dronePort)
+ run(): void
- processIncidentQueue(): void
- sendEventToDrone(Event event): boolean
+ main(String[] args): void
}
}

FireIncident --> EventReader
FireIncident --> Event
Event --> Severity
Event --> EventType
Event --> Drone
EventReader --> Event
Zones --> Coordinates
DroneSubsystem --> Drone
DroneSubsystem --> Zones
Drone --> BayController
Drone --> DroneState
DroneState <|.. IdleState
DroneState <|.. EnRouteState
DroneState <|.. DroppingAgentState
DroneState <|.. RefillingState
DroneState <|.. FaultedState
DroneState <|.. ReturningState
Drone --> CommunicationDroneToSubsystem
Scheduler --> Event
Scheduler --> DroneSubsystem
@enduml
