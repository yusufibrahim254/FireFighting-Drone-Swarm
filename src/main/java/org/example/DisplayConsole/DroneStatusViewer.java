package org.example.DisplayConsole;

import org.example.DroneSystem.Drone;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the Drone Status Viewer, monitors the drones and their states during the simulation
 */
public class DroneStatusViewer extends JPanel {
    private final Map<Drone, JPanel> dronePanels = new HashMap<>();
    private final Map<Drone, JProgressBar> agentBars = new HashMap<>();
    private final Map<Drone, JProgressBar> batteryBars = new HashMap<>();
    private static final int MAX_AGENT_CAPACITY = 15;

    /**
     * Constructor for the DroneStatusViewer
     */
    public DroneStatusViewer(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Drone Status Viewer"));
    }

    /**
     * Add drone to the viewer
     * @param drone the drone
     */
    public void addDroneToViewer(Drone drone){
        // format the drone string to html
        String formattedText = "<html>" + drone.toString().replace("\n", "<br>") + "</html>";

        // if drone is not already in the panel, add it
        if (!dronePanels.containsKey(drone)) {
            JPanel dronePanel = new JPanel();
            dronePanel.setLayout(new BoxLayout(dronePanel, BoxLayout.Y_AXIS));

            // box for the drone info
            dronePanel.setBorder(BorderFactory.createTitledBorder("Drone " + drone.getId()));
            JLabel droneLabel = new JLabel(formattedText);
            droneLabel.setVerticalAlignment(SwingConstants.CENTER);

            // track and measure the drone tank and battery
            JProgressBar agentBar = new JProgressBar(0, MAX_AGENT_CAPACITY);
            agentBar.setStringPainted(true);
            agentBar.setToolTipText("Tank (L)");

            JProgressBar batteryBar = new JProgressBar(0, 100);
            batteryBar.setStringPainted(true);
            batteryBar.setToolTipText("Battery (%)");

            agentBars.put(drone, agentBar);
            batteryBars.put(drone, batteryBar);

            // add them all in to the drone panel
            dronePanel.add(droneLabel);
            dronePanel.add(agentBar);
            dronePanel.add(batteryBar);
            dronePanels.put(drone, dronePanel);
            this.add(dronePanel);
        } else { // already in the viewer, so update its info
            JPanel dronePanel = dronePanels.get(drone);
            for (Component comp : dronePanel.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setText(formattedText);
                    break;
                }
            }
        }
        // initialize the bars (tank and battery)
        agentBars.get(drone).setValue((int) drone.getAgentCapacity());
        agentBars.get(drone).setString(String.format("Tank: %.1fL / %dL", drone.getAgentCapacity(), MAX_AGENT_CAPACITY));

        batteryBars.get(drone).setValue((int) drone.getBatteryLevel());
        batteryBars.get(drone).setString(String.format("Battery: %.0f%%", drone.getBatteryLevel()));


        this.revalidate();
        this.repaint();
    }

    /**
     * Places the viewer into a scroll pane to allow it to be scrollable
     * @return the scroll pane
     */
    public JScrollPane initializeViewer() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        return scrollPane;
    }
}
