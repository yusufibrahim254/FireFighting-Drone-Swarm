
import org.example.Scheduler;
import org.example.FireIncidentSubsystem.FireIncident;
import org.example.DroneSubsystem;

/**
 * The Main class initializes and starts the firefighting drone swarm system.
 * It creates an instance of Scheduler, FireIncident, and DroneSubsystem.
 * It is designed to run these instances in separate threads to simulate a real-life, real-time concurrent system.
 */

public class Main {

    /**
     * The starting point of the firefighting drone swarm system.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        FireIncident fireIncident = new FireIncident(scheduler);
        DroneSubsystem droneSubsystem = new DroneSubsystem(scheduler);

        Thread schedulerThread = new Thread(scheduler);
        Thread fireThread = new Thread(fireIncident);
        Thread droneThread = new Thread(droneSubsystem);

        schedulerThread.start();
        fireThread.start();
        droneThread.start();

    }
}
