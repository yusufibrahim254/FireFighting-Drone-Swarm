package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.util.LinkedList;

/**
 * The main window for the simulation, the home page
 */
public class Home extends JFrame {
    private ConsoleView view;

    /**
     * Constructor for the Home gui
     */
    public Home(){
        setTitle("FireFighting Drone Swarm Simulation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ConsoleController controller = new ConsoleController();
        String zonesFilePath = "docs/sample_zone_file.csv";
        LinkedList<Zone> zones = controller.getZones(zonesFilePath);

        view = new ConsoleView(zones);
        add(view);
        setVisible(true);
    }

    public ConsoleView getView() {
        return view;
    }

    public void setView(ConsoleView view) {
        this.view = view;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Home::new);
    }
}
