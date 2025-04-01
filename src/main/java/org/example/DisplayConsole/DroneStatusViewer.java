package org.example.DisplayConsole;

import org.example.DroneSystem.Drone;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DroneStatusViewer extends JPanel {
    private final Map<Drone, JPanel> dronePanels = new HashMap<>();

    public DroneStatusViewer(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Drone Status Viewer"));
    }

    public void addDroneToViewer(Drone drone){
        String formattedText = "<html>" + drone.toString().replace("\n", "<br>") + "</html>";

        if (!dronePanels.containsKey(drone)) {
            JPanel dronePanel = new JPanel();
            dronePanel.setBorder(BorderFactory.createTitledBorder("Drone " + drone.getId()));
            JLabel droneLabel = new JLabel(formattedText);
            droneLabel.setVerticalAlignment(SwingConstants.CENTER);
            dronePanel.add(droneLabel, BorderLayout.CENTER);
            dronePanels.put(drone, dronePanel);
            this.add(dronePanel);
        } else {
            JPanel dronePanel = dronePanels.get(drone);
            JLabel droneLabel = (JLabel) dronePanel.getComponent(0);
            droneLabel.setText(formattedText);
        }

        this.revalidate();
        this.repaint();
    }

    public JScrollPane initalizeViewer() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        return scrollPane;
    }
}
