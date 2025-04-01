package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * The main window for the simulation, the home page
 */
public class Home extends JFrame {
    private ConsoleView view;
    private DroneStatusViewer status;

    /**
     * Constructor for the Home gui
     */
    public Home(){
        setTitle("FireFighting Drone Swarm Simulation");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ConsoleController controller = new ConsoleController();
        String zonesFilePath = "docs/sample_zone_file.csv";
        LinkedList<Zone> zones = controller.getZones(zonesFilePath);

        JLabel label = new JLabel("FireFighting Drone Swarm - Group 8 Lab A1");
        label.setHorizontalAlignment(SwingConstants.CENTER);


        Legend legend = new Legend();
        status = new DroneStatusViewer();

        view = new ConsoleView(zones);
        add(view, BorderLayout.CENTER);
        add(legend, BorderLayout.LINE_END);
        add(status, BorderLayout.LINE_START);
        add(label, BorderLayout.PAGE_START);

        setVisible(true);
    }



    public ConsoleView getView() {
        return view;
    }

    public void setView(ConsoleView view) {
        this.view = view;
    }

    public DroneStatusViewer getStatus() {
        return status;
    }

    public void setStatus(DroneStatusViewer status) {
        this.status = status;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Home::new);
    }
}
