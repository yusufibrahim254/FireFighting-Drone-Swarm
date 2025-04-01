package org.example.DisplayConsole;

import org.example.DroneSystem.Drone;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class DroneStatusViewer extends JPanel {
    private final Map<Drone, JLabel> droneLabels = new HashMap<>();

    public DroneStatusViewer(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Drone Status Viewer"));
    }

    public void addDroneToViewer(Drone drone){
        String formattedText = "<html>" + drone.toString().replace("\n", "<br>") + "</html>";

        if (!droneLabels.containsKey(drone)) {
            // If drone is new, create and add a new JLabel
            JLabel droneLabel = new JLabel(formattedText);
            droneLabel.setVerticalAlignment(SwingConstants.CENTER);
            droneLabels.put(drone, droneLabel);
            this.add(droneLabel);
        } else {
            // If drone already exists, update its label text
            droneLabels.get(drone).setText(formattedText);
        }

        // Refresh UI
        this.revalidate();
        this.repaint();
    }
}
