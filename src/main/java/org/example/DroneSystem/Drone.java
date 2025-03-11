package org.example.DroneSystem;

import org.example.FireIncidentSubsystem.Event;
import org.example.DroneSystem.DroneSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The Drone class represents a drone that can be assigned tasks and moves towards a target position.
 */
public class Drone implements Runnable {
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
    private DroneSubsystem droneSubsystem;
    private int[] currentPosition = {0, 0}; // Current position of the drone
    private int[] incidentPosition; // Used by the drone to go back when refilled in case it was not extinguished the first time
    private int[] targetPosition = null; // Target position of the drone
    private int[] lastSentPosition = {0, 0}; // Last position sent to the DroneSubsystem
    private Event currentEvent; // Current event assigned to the drone
    double batteryDepletionRate;
    public Drone(int id, double initialCapacity, DroneSubsystem droneSubsystem, double batteryDepletionRate) {
        this.id = id;
        this.battery = 100;
        this.bayController = new BayController();
        this.maxAgentCapacity = initialCapacity;
        this.agentCapacity = initialCapacity;
        this.remainingWaterNeeded = 0;
        this.currentEvent = null;
        this.state = new IdleState();
        this.droneSubsystem = droneSubsystem;
        this.batteryDepletionRate = batteryDepletionRate;
    }

    public double getBatteryDepletionRate() {
        return batteryDepletionRate;
    }

    public int getId() {
        return id;
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

    public double getBatteryLevel() {
        return battery;
    }
    public void setBatterLevel(int newLevel){
        this.battery = newLevel;
    }

    public DroneState getState() {
        return state;
    }

    public void setState(DroneState state) {
        this.state = state;
    }
    public void deletegateJob(){
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
    public int[] getCurrentPosition() {
        return currentPosition;
    }

    public void setTargetPosition(int[] targetPosition) {
        this.targetPosition = targetPosition;
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
                    System.out.println("The drone " +this.id+"'s position ("+  currentPosition[0] + ", " + currentPosition[1]+ ") The target position "+this.targetPosition[0]+","+this.targetPosition[1]);
                    moveTowardsTarget();
                    if (hasReachedTarget()) {
                        state.arrive(this); // Transition to DroppingAgentState
                    }
                } else if (state instanceof DroppingAgentState) {
                    System.out.println("\n");
                    state.displayState(this);
                    state.dropAgent(this); // Drop Agent
                } else if (state instanceof RefillingState) {
                    System.out.println("\n");
                    state.displayState(this);
                    state.refill(this);
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

//                // Send position update only if the target has changed
//                if (hasTargetChanged()) {
//                    sendPositionToSubsystem();
//                    lastSentPosition[0] = currentPosition[0]; // Update last sent position
//                    lastSentPosition[1] = currentPosition[1];
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Moves the drone 1 meter closer to its target position until it reaches the target.
     */
    public void moveTowardsTarget() {
        double cruisingSpeed = 4; // m/s (cruise speed)
        double acceleration = 7; // m/s²
        double timeStep = 1; // seconds per step
        double currentSpeed = 0; // Start at rest
        double timeElapsed = 0; // Time elapsed
        boolean isCruising = false; // Flag to track if the drone has reached cruising speed

        while (targetPosition != null && !hasReachedTarget()) {
            double distanceToTarget = Math.sqrt(
                    Math.pow(targetPosition[0] - currentPosition[0], 2) +
                            Math.pow(targetPosition[1] - currentPosition[1], 2)
            );

            if (distanceToTarget > 0) {
                double directionX = (targetPosition[0] - currentPosition[0]) / distanceToTarget;
                double directionY = (targetPosition[1] - currentPosition[1]) / distanceToTarget;

                //Accelerate first
                if (!isCruising) {
                    currentSpeed = acceleration;
                    timeElapsed += timeStep;
                }
                //Once the first few seconds of acceleration elapsed, go down to cruising speed
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
//                System.out.println("[Drone " + id + "] Moving towards target: (" + currentPosition[0] + ", " + currentPosition[1] + ") at speed " + currentSpeed + " m/s");
//                System.out.println("[Drone " + id + "] Battery Level: "+ this.battery);

                try {
                    Thread.sleep(1000); // Pause for 1 second
                } catch (InterruptedException e) {
                    System.err.println("[Drone " + id + "] Movement interrupted.");
                    Thread.currentThread().interrupt(); // Restore interrupted state
                    break;
                }
            }
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
//                System.out.println("[Drone " + id + " -> DroneSubsystem] Sent position data: " + positionData + ", Target position: (" + targetPosition[0] + ", " + targetPosition[1] + ")");
            } else {
                System.out.println("[Drone " + id + "] Sent position data: " + positionData +
                        ", No target position set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
        if(currentEvent != null){
            this.remainingWaterNeeded = currentEvent.getSeverityWaterAmount();
        }
    }
    public Event getCurrentEvent(){
        return this.currentEvent;
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

    public int[] getIncidentPosition() {
        return incidentPosition;
    }

    public void setIncidentPosition(int[] incidentPosition) {
        this.incidentPosition = incidentPosition;
    }
}