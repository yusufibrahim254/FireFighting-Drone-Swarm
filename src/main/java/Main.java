
import org.example.Scheduler;
import org.example.FireIncidentSubsystem.FireIncident;
import org.example.DroneSubsystem;

public class Main {
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
