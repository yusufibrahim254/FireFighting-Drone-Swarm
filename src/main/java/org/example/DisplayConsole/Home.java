package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Set;

public class Home extends JFrame {

    public Home(){
        setTitle("FireFighting Drone Swarm Simulation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ConsoleController controller = new ConsoleController();
        String zonesFilePath = "docs/sample_zone_file.csv";
        LinkedList<Zone> zones = controller.getZones(zonesFilePath);

        ConsoleView view = new ConsoleView(zones);
        add(view);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Home::new);
    }
}
