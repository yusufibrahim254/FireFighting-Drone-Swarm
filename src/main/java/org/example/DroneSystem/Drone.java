package org.example.DroneSystem;


import org.example.FireIncidentSubsystem.Event;
import org.example.DroneSystem.DroneSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Drone class represents a drone that can be assigned tasks and moves towards a target position.
 */
public class Drone implements Runnable {
    private Timer faultTimer;
    private static final int FAULT_TIMEOUT = 600000; // Timeout duration: 60 seconds
    private DroneSubsystem droneSubsystem;

    private final int id;
    /**
     * Current battery level of the drone, represented as a percentage (0-100)
     */
    private double battery;

    /**
     * Current amount of agent available in the drone (Liters)
     */
    private double agentCapacity;

    /**
     * Maximum amount of agent that the drone can carry (Liters)
     */
    private final double maxAgentCapacity;


    /**
     * The current state of the Drone. (enum DroneState)
     */
    private DroneState state;

    /**
     * Controller for the drone's bay doors
     */
    private final BayController bayController;
    private double remainingWaterNeeded;
    private int[] currentPosition = {0, 0}; // Current position of the drone
    private int[] incidentPosition; // Used by the drone to go back when refilled in case it was not extinguished the first time
    private int[] targetPosition = null; // Target position of the drone
    private int[] lastSentPosition = {0, 0}; // Last position sent to the DroneSubsystem
    private Event currentEvent; // Current event assigned to the drone
    private double batteryDepletionRate;

    public Drone(int id, double initialCapacity, DroneSubsystem droneSubsystem, double batteryDepletionRate) {
        this.id = id;
        this.battery = 100;
        this.bayController = new BayController();
        this.maxAgentCapacity = initialCapacity;
        this.agentCapacity = initialCapacity;
        this.remainingWaterNeeded = 0;
        this.currentEvent = null;
        this.state = new IdleState();
        this.batteryDepletionRate = batteryDepletionRate;
        this.droneSubsystem = droneSubsystem;
    }

    // Setter for the drone's current position
    public void setCurrentPosition(int[] position) {
        if (position != null && position.length == 2) {
            this.currentPosition[0] = position[0];
            this.currentPosition[1] = position[1];
        } else {
            throw new IllegalArgumentException("Position must be a non-null array of length 2.");
        }
    }

    public void delegateJob(){
        if (state instanceof DroppingAgentState && currentEvent != null) {
            try (DatagramSocket socket = new DatagramSocket()) {
                String type = CommunicationDroneToSubsystem.JOB_DELEGATION.name();
                String droneID = ""+ id;
                String message = type+":"+droneID;
                byte[] sendData = message.getBytes();
                InetAddress subsystemAddress = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, subsystemAddress, 6001);
                socket.send(sendPacket);
                // Print current position and target position
                System.out.println("[Drone " + id + "] is trying to delegate job.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Checks if the target has changed since the last update.
     *
     * @return True if the target has changed, false otherwise.
     */
    private boolean hasTargetChanged() {
        return targetPosition != null && (currentPosition[0] != lastSentPosition[0] || currentPosition[1] != lastSentPosition[1]);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Update position every second
                Thread.sleep(1000);

                // Handle state transitions
                if (state instanceof IdleState) {
                    // Do nothing, wait for an event
                    if(currentEvent != null){
                        this.state.dispatch(this);
                    }
                } else if (state instanceof EnRouteState) {
                    System.out.println("\n");
                    state.displayState(this);
                    System.out.println("The drone " +this.id+"'s position ("+  currentPosition[0] + ", " + currentPosition[1]+ ") The target position "+this.targetPosition[0]+","+this.targetPosition[1]+ " With Event ID: "+this.currentEvent.getId());
                    String returnValue = moveTowardsTarget();
                    if(returnValue.equals("DRONE_STUCK")){
                        // Turn off the drone at whichever location it is
                        this.setState(new FaultedState());
                        state.displayState(this);
                        this.setCurrentEvent(null);
                    }else if(returnValue.equals("NOZZLE_JAMMED")){
                        // Then go back to original location and reset and should be able to take tasks
                        cleandAndSendEventBackToDroneSubsystem();
                        this.setCurrentEvent(null);
                        this.setState(new FaultedState());
                        state.displayState(this);
                        this.setTargetPosition(new int[]{0, 0});
                        moveTowardsTarget();
                        System.out.println("Drone is resetting nozzle...");
                        this.setState(new IdleState());
                        state.displayState(this);
                    }
                    if (hasReachedTarget()) {
                        state.arrive(this); // Transition to DroppingAgentState
                    }
                } else if (state instanceof DroppingAgentState) {
                    System.out.println("\n");
                    state.displayState(this);
                    Event handledEvent = currentEvent;
                    droneSubsystem.getDroneStatusViewer().addDroneToViewer(this);
                    double water = state.dropAgent(this); // Drop Agent

                    if (currentEvent == null && water == 0){
                        System.out.println("Drone " + id + " completed Event " + handledEvent.getId() + ", returning to base.");
                        droneSubsystem.updateFireZones(handledEvent);
                    }
                } else if (state instanceof RefillingState) {
                    System.out.println("\n");
                    state.displayState(this);
                    state.refill(this);
                    droneSubsystem.getDroneStatusViewer().addDroneToViewer(this);
                } else if (state instanceof ReturningState) {
                    System.out.println("\n");
                    state.displayState(this);
                    moveTowardsTarget();
                    if (hasReachedTarget()) {
                        state.arrive(this); // Transition to IdleState
                    }
                } else if (state instanceof FaultedState) {
                    // Handle fault state

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Moves the drone 1 meter closer to its target position until it reaches the target.
     */
    public String moveTowardsTarget() {
        startFaultTimer(); // Start the timer when movement begins
        double cruisingSpeed = 4; // m/s (cruise speed)
        double acceleration = 7; // m/s²
        double timeStep = 1; // seconds per step
        double currentSpeed = 0; // Start at rest
        double timeElapsed = 0; // Time elapsed
        boolean isCruising = false; // Flag to track if the drone has reached cruising speed

        // Calculate total distance to target
        double totalDistance = Math.sqrt(
                Math.pow(targetPosition[0] - currentPosition[0], 2) +
                        Math.pow(targetPosition[1] - currentPosition[1], 2)
        );

        // Check if drone should get stuck (1/3 of the way to target)
        boolean shouldGetStuck = currentEvent != null &&
                "DRONE_STUCK".equals(currentEvent.getFault()) &&
                !(state instanceof ReturningState);

        double distanceTraveledBeforeStuck = 0;
        boolean isStuck = false;
        long stuckStartTime = 0;

        while (targetPosition != null && !hasReachedTarget()) {
            double distanceToTarget = Math.sqrt(
                    Math.pow(targetPosition[0] - currentPosition[0], 2) +
                            Math.pow(targetPosition[1] - currentPosition[1], 2)
            );

            // Check if we should simulate getting stuck (only when going to target, not returning)
            if (shouldGetStuck && !isStuck) {
                distanceTraveledBeforeStuck = totalDistance - distanceToTarget;
                if (distanceTraveledBeforeStuck >= totalDistance / 3) {
                    isStuck = true;
                    stuckStartTime = System.currentTimeMillis();
                    System.out.println("[Drone " + id + "] STUCK! Remaining in position for 2 seconds");
                }
            }

            // If stuck, wait for 2 seconds then return to base
            if (isStuck) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - stuckStartTime < 2000) {
//                    drone.setTargetPosition(new int[]{0, 0});
                    // Still stuck, don't move
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                } else {
                    cleandAndSendEventBackToDroneSubsystem();

                    return "DRONE_STUCK";
                }
            }

            if (distanceToTarget > 0) {
                double directionX = (targetPosition[0] - currentPosition[0]) / distanceToTarget;
                double directionY = (targetPosition[1] - currentPosition[1]) / distanceToTarget;

                // Accelerate first
                if (!isCruising) {
                    currentSpeed = acceleration;
                    timeElapsed += timeStep;
                }
                // Once the first few seconds of acceleration elapsed, go down to cruising speed
                if (timeElapsed > 3) {
                    isCruising = true;
                    currentSpeed = cruisingSpeed;
                }

                // Calculate next position
                double nextX = currentPosition[0] + directionX * currentSpeed * timeStep;
                double nextY = currentPosition[1] + directionY * currentSpeed * timeStep;

                if (Math.abs(nextX - targetPosition[0]) < 1) {
                    nextX = targetPosition[0];
                } else if ((nextX > targetPosition[0] && directionX > 0) || (nextX < targetPosition[0] && directionX < 0)) {
                    nextX = targetPosition[0];
                }

                if (Math.abs(nextY - targetPosition[1]) < 1) {
                    nextY = targetPosition[1];
                } else if ((nextY > targetPosition[1] && directionY > 0) || (nextY < targetPosition[1] && directionY < 0)) {
                    nextY = targetPosition[1];
                }

                // Update current position
                currentPosition[0] = (int) nextX;
                currentPosition[1] = (int) nextY;

                // Deplete battery based on distance traveled
                double distanceTraveled = Math.sqrt(Math.pow(directionX * currentSpeed * timeStep, 2) +
                        Math.pow(directionY * currentSpeed * timeStep, 2));
                this.battery -= distanceTraveled * batteryDepletionRate;

                sendPositionToSubsystem();

                // Cancels the timer if the drone successfully reaches its target.
                if (hasReachedTarget()) {
                    cancelFaultTimer();
                }

                try {
                    Thread.sleep(1000); // Pause for 1 second
                } catch (InterruptedException e) {
                    System.err.println("[Drone " + id + "] Movement interrupted.");
                    Thread.currentThread().interrupt(); // Restore interrupted state
                    break;
                }
            }
        }

        if(this.currentEvent != null && this.currentEvent.getFault().equals("NOZZLE_JAMMED")){
            return "NOZZLE_JAMMED";
        }
        return "TARGET_REACHED";
    }

    /**
     * Starts a timer to track if the drone reaches its target within a given time frame.
     * If the drone doesn't reach the target within the specified timeout, a fault is triggered
     * and the drone's state is changed to FaultedState.
     */
    private void startFaultTimer() {
        if (faultTimer != null) {
            faultTimer.cancel(); // Cancel any existing timer
        }

        faultTimer = new Timer();
        faultTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!hasReachedTarget()) {
                    System.out.println("[Drone " + id + "] Fault detected: Failed to reach target in time.");
                    setState(new FaultedState());
                }
            }
        }, FAULT_TIMEOUT);
    }

    /**
     * Cancels the currently running fault timer, if one exists.
     * This is called when the drone successfully reaches its target before the timer expires,
     * preventing the fault from being triggered.
     */
    private void cancelFaultTimer() {
        if (faultTimer != null) {
            faultTimer.cancel();
            faultTimer = null;
        }
    }

    public void cleandAndSendEventBackToDroneSubsystem(){
        // Create cleaned event
        Event newEvent = new Event(
                this.currentEvent.getId(),
                this.currentEvent.getTime(),
                this.currentEvent.getZoneId(),
                this.currentEvent.getEventType(),
                this.currentEvent.getSeverityLevel(),
                null);

        // Send the event back to DroneSubsystem
        try (DatagramSocket socket = new DatagramSocket()) {
            String type = CommunicationDroneToSubsystem.EVENT_RETURN.name();
            String message = type + ":" + newEvent.serialize();
            byte[] sendData = message.getBytes();
            InetAddress subsystemAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, subsystemAddress, 6001);
            socket.send(sendPacket);
            System.out.println("[Drone " + id + "] Sent event back to DroneSubsystem because it faulted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the drone's current position to the DroneSubsystem.
     */
    private void sendPositionToSubsystem() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String type = CommunicationDroneToSubsystem.LOCATION_UPDATE.name();
            String positionData = type+":"+id + "," + currentPosition[0] + "," + currentPosition[1];
            byte[] sendData = positionData.getBytes();
            InetAddress subsystemAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, subsystemAddress, 6001);
            socket.send(sendPacket);
            // Print current position and target position
            if (targetPosition != null) {
//               System.out.println("[Drone " + id + " -> DroneSubsystem] Sent position data: " + positionData + ", Target position: (" + targetPosition[0] + ", " + targetPosition[1] + ")");
                droneSubsystem.getDroneStatusViewer().addDroneToViewer(this);
            } else {
                System.out.println("[Drone " + id + "] Sent position data: " + positionData +
                        ", No target position set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Checks if the drone has reached its target position.
     *
     * @return True if the drone is within 1 meter of the target, false otherwise.
     */
    private boolean hasReachedTarget() {
        if (targetPosition == null) {
            return false; // No target assigned
        }

        // Calculate the Euclidean distance to the target
        double distance = Math.sqrt(
                Math.pow(targetPosition[0] - currentPosition[0], 2) +
                        Math.pow(targetPosition[1] - currentPosition[1], 2)
        );

        return distance <= 1.0; // Considered reached if within 1 meter
    }

    public double getMaxAgentCapacity() {
        return maxAgentCapacity;
    }

    public double getAgentCapacity() {
        return agentCapacity;
    }

    public void setAgentCapacity(double agentCapacity) {
        this.agentCapacity = agentCapacity;
    }

    public BayController getBayController() {
        return bayController;
    }

    public double getRemainingWaterNeeded() {
        return remainingWaterNeeded;
    }

    public void setRemainingWaterNeeded(double remainingWaterNeeded) {
        this.remainingWaterNeeded = remainingWaterNeeded;
    }

    public double getBatteryLevel() {
        return battery;
    }
    public void setBatteryLevel(int newLevel){
        this.battery = newLevel;
    }

    public DroneState getState() {
        return state;
    }

    public void setState(DroneState state) {
        this.state = state;
        droneSubsystem.updateDroneStateInConsole(this.id, state);
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;

        if(currentEvent != null){
            currentEvent.setAssignedDrone(this);
            this.remainingWaterNeeded = currentEvent.getSeverityWaterAmount();
        } else {
            System.out.println("Drone "+this.id+"'s current event is now NULL");
            System.out.println("Drone " + this.id + "'s capacity is: " + agentCapacity);
            this.remainingWaterNeeded = 0;
        }
    }
    public Event getCurrentEvent(){
        return this.currentEvent;
    }

    public int[] getIncidentPosition() {
        return incidentPosition;
    }

    public void setIncidentPosition(int[] incidentPosition) {
        this.incidentPosition = incidentPosition;
    }

    public double getBatteryDepletionRate() {
        return batteryDepletionRate;
    }

    public int getId() {
        return id;
    }
    public int[] getCurrentPosition() {
        return currentPosition;
    }

    public void setTargetPosition(int[] targetPosition) {
        this.targetPosition = targetPosition;
    }

    public int[] getTargetPosition() {
        return targetPosition;
    }

    public int[] getLastSentPosition() {
        return lastSentPosition;
    }

    public void setLastSentPosition(int[] lastSentPosition) {
        this.lastSentPosition = lastSentPosition;
    }

    public void setBatteryDepletionRate(double batteryDepletionRate) {
        this.batteryDepletionRate = batteryDepletionRate;
    }

    @Override
    public String toString() {
        if (currentEvent == null){
            return "agentCapacity=" + agentCapacity + " L" +
                    "\n state=" + state.getState(this) +
                    "\n currentPosition=" + Arrays.toString(currentPosition) +
                    "\n Returning to origin at [0,0]";
        }
        return "agentCapacity=" + agentCapacity + " L" +
                "\n state=" + state.getState(this) +
                "\n currentPosition=" + Arrays.toString(currentPosition) +
                "\n currentEvent=" + currentEvent.getId() +
                "\n target zone=" + currentEvent.getZoneId();
    }
}