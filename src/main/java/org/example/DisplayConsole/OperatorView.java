package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.FireIncident;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents the view for the operator GUI, the presentation and interface to get inputs from operator
 */
public class OperatorView extends JFrame {
    private FireIncident fireIncidentSubsystem;

    /**
     * Constructor for the operator GUI
     * @param fireIncidentSubsystem the fire incident subsystem to send event requests to
     */
    public OperatorView(FireIncident fireIncidentSubsystem){
        this.fireIncidentSubsystem = fireIncidentSubsystem;
        setTitle("FireFighting Drone Swarm Operator System");
        setSize(450, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1,2));
        OperatorController controller = new OperatorController(this);
        operatorButtons(controller);
        setResizable(false);
        setVisible(true);
    }

    /**
     * The operator buttons to take in the inputs
     * @param controller the operator controller handling logic
     */
    public void operatorButtons(OperatorController controller){
        JButton requestDroneButton = new JButton("Request Drone");
        JButton fireDetectedButton = new JButton("Fire Detected");

        // remove focus off buttons and color code the text
        requestDroneButton.setFocusable(false);
        fireDetectedButton.setFocusable(false);
        requestDroneButton.setForeground(Color.BLUE);
        fireDetectedButton.setForeground(Color.RED);

        // action listener for if operator requests a drone
        requestDroneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendDroneRequest();
            }
        });

        // action listener for if operator detects a fire
        fireDetectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendFireDetected();
            }
        });

        this.add(requestDroneButton);
        this.add(fireDetectedButton);
    }

    /**
     * Gets the fire incident subsystem reference
     * @return the reference to the fire incident subsystem
     */
    public FireIncident getFireIncidentSubsystem() {
        return fireIncidentSubsystem;
    }
}
