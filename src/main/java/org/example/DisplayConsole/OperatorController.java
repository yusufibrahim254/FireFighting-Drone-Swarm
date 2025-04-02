package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Event;
import org.example.FireIncidentSubsystem.Helpers.EventType;
import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;


public class OperatorController {
    private OperatorView view;
    private ConsoleController controller;
    public OperatorController(OperatorView view){
        this.view = view;
        this.controller = new ConsoleController();
    }

    public void generateEvent(EventType eventType){
        int eventId = new Random().nextInt(900) + 100;

        JTextField timeField = new JTextField();

//        Integer[] zoneIds = {1, 2, 3, 4};
        LinkedList<Zone> zoneIds = controller.getZones("docs/sample_zone_file.csv");
        JComboBox<Object> zoneDropdown = new JComboBox<>(zoneIds.toArray());

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
            Zone zoneId = (Zone) zoneDropdown.getSelectedItem();
            System.out.println(zoneId);
            String severity = severityDropdown.getSelectedItem().toString();
            String fault = faultDropdown.getSelectedItem().toString();

            EventType faultType = EventType.valueOf(fault);

            Event event = new Event(eventId, time, zoneId.getZoneId(), eventType, severity, fault);

            try {
                view.getFireIncidentSubsystem().manualSendEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendDroneRequest() {
        generateEvent(EventType.DRONE_REQUEST);
    }


    public void sendFireDetected() {
        generateEvent(EventType.FIRE_DETECTED);
    }
}
