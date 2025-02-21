
import org.example.Scheduler;
import org.example.FireIncidentSubsystem.FireIncident;
import org.example.DroneSystem.DroneSubsystem;

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
        DroneSubsystem droneSubsystem = new DroneSubsystem();
        Scheduler scheduler = new Scheduler(droneSubsystem);
        FireIncident fireIncident = new FireIncident(scheduler);

        Thread schedulerThread = new Thread(scheduler);
        Thread fireThread = new Thread(fireIncident);

        schedulerThread.start();
        fireThread.start();


    }
}
