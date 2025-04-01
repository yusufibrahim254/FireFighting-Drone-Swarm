package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.FireIncident;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OperatorView extends JFrame {
    private FireIncident fireIncidentSubsystem;

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

    public void operatorButtons(OperatorController controller){
        JButton requestDroneButton = new JButton("Request Drone");
        JButton fireDetectedButton = new JButton("Fire Detected");

        requestDroneButton.setFocusable(false);
        fireDetectedButton.setFocusable(false);

        requestDroneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendDroneRequest();
            }
        });
        fireDetectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendFireDetected();
            }
        });

        this.add(requestDroneButton);
        this.add(fireDetectedButton);
    }

    public FireIncident getFireIncidentSubsystem() {
        return fireIncidentSubsystem;
    }
}
