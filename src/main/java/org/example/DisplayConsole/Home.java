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
    private final EventDashboard dashboard;

    /**
     * Constructor for the Home gui
     */
    public Home(){
        setTitle("FireFighting Drone Swarm Simulation");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ConsoleController controller = new ConsoleController();
        LinkedList<Zone> zones = controller.getZones();

        JLabel label = new JLabel("FireFighting Drone Swarm - Group 8 Lab A1");
        label.setHorizontalAlignment(SwingConstants.CENTER);


        Legend legend = new Legend();
        dashboard = new EventDashboard();
        status = new DroneStatusViewer();


        view = new ConsoleView(zones, controller);
        int[] grid = getGridMetrics();
        view.setGrid(grid[0], grid[1]);
        JScrollPane pane = new JScrollPane(view);

        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controller.setView(view);
        add(pane, BorderLayout.CENTER);
        add(legend, BorderLayout.LINE_END);
        add(status.initializeViewer(), BorderLayout.LINE_START);
        add(label, BorderLayout.PAGE_START);
        add(dashboard, BorderLayout.PAGE_END);

        setVisible(true);
    }


    /**
     * Gets the console view
     * @return the console view
     */
    public ConsoleView getView() {
        return view;
    }

    /**
     * Gets the drone status viewer
     * @return the drone status viewer
     */
    public DroneStatusViewer getStatus() {
        return status;
    }

    /**
     * Gets the event dashboard
     * @return the event dashboard
     */
    public EventDashboard getDashboard() {
        return dashboard;
    }

    /**
     * Get the metrics of the size of the grid
     * @return the metrics for the grid
     */
    public int[] getGridMetrics(){
        return view.getController().getZoneSize();
    }

}
