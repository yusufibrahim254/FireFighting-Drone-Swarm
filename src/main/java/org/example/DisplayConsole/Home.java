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

        int[] grid = getGridMetrics();
        view = new ConsoleView(zones, controller, grid[0], grid[1]);
        JScrollPane pane = new JScrollPane(view);

        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controller.setView(view);
        add(pane, BorderLayout.CENTER);
        add(legend, BorderLayout.LINE_END);
        add(status.initalizeViewer(), BorderLayout.LINE_START);
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

    public int[] getGridMetrics(){
        JTextField widthField = new JTextField();
        JTextField heightField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Grid Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Grid Height:"));
        panel.add(heightField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Grid Size", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int gridWidth = Integer.parseInt(widthField.getText());
            int gridHeight = Integer.parseInt(heightField.getText());
            return new int[]{gridWidth, gridHeight};
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Home::new);
    }
}
