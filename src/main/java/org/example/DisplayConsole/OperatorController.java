package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;

import javax.swing.*;
import java.io.IOException;
import java.util.Random;


public class OperatorController {
    private OperatorView view;
    public OperatorController(OperatorView view){
        this.view = view;
    }

    public void sendDroneRequest() {
        int eventId = new Random().nextInt(900) + 100;

        JTextField timeField = new JTextField();

        Integer[] zoneIds = {1, 2, 3, 4};
        JComboBox<Integer> zoneDropdown = new JComboBox<>(zoneIds);

        String[] severityLevels = {"Low", "Moderate", "High"};
        JComboBox<String> severityDropdown = new JComboBox<>(severityLevels);

        EventType[] faultTypes = {
                EventType.NO_FAULT,
                EventType.DRONE_STUCK,
                EventType.NOZZLE_JAMMED,
                EventType.CORRUPTED_MESSAGE
        };
        JComboBox<EventType> faultDropdown = new JComboBox<>(faultTypes);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Time:"));
        panel.add(timeField);
        panel.add(new JLabel("Zone ID:"));
        panel.add(zoneDropdown);
        panel.add(new JLabel("Severity:"));
        panel.add(severityDropdown);
        panel.add(new JLabel("Fault Type:"));
        panel.add(faultDropdown);

        int result = JOptionPane.showConfirmDialog(null, panel, "Create an Event",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String time = timeField.getText();
            int zoneId = (Integer) zoneDropdown.getSelectedItem();
            String severity = severityDropdown.getSelectedItem().toString();
            String fault = faultDropdown.getSelectedItem().toString();

            EventType faultType = EventType.valueOf(fault); // Ensure enum values match exactly

            Event event = new Event(eventId, time, zoneId, EventType.DRONE_REQUEST, severity, fault);

            try {
                view.getFireIncidentSubsystem().manualSendEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void sendFireDetected() {
        // implement sending a event for sending a fire detected message
        int eventId = new Random().nextInt(900) + 100;

        JTextField timeField = new JTextField();

        Integer[] zoneIds = {1, 2, 3, 4};
        JComboBox<Integer> zoneDropdown = new JComboBox<>(zoneIds);

        String[] severityLevels = {"Low", "Moderate", "High"};
        JComboBox<String> severityDropdown = new JComboBox<>(severityLevels);

        EventType[] faultTypes = {
                EventType.NO_FAULT,
                EventType.DRONE_STUCK,
                EventType.NOZZLE_JAMMED,
                EventType.CORRUPTED_MESSAGE
        };
        JComboBox<EventType> faultDropdown = new JComboBox<>(faultTypes);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Time:"));
        panel.add(timeField);
        panel.add(new JLabel("Zone ID:"));
        panel.add(zoneDropdown);
        panel.add(new JLabel("Severity:"));
        panel.add(severityDropdown);
        panel.add(new JLabel("Fault Type:"));
        panel.add(faultDropdown);

        int result = JOptionPane.showConfirmDialog(null, panel, "Create an Event",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String time = timeField.getText();
            int zoneId = (Integer) zoneDropdown.getSelectedItem();
            String severity = severityDropdown.getSelectedItem().toString();
            String fault = faultDropdown.getSelectedItem().toString();

            EventType faultType = EventType.valueOf(fault); // Ensure enum values match exactly

            Event event = new Event(eventId, time, zoneId, EventType.FIRE_DETECTED, severity, fault);

            try {
                view.getFireIncidentSubsystem().manualSendEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
