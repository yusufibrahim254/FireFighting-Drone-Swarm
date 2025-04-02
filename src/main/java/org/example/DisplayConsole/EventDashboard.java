package org.example.DisplayConsole;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the event dashboard, monitors and tracks all events occurring
 * during the simulation and progression bar to completion
 */
public class EventDashboard extends JPanel {
    private final Map<Integer, JProgressBar> eventProgressBars;
    private final Map<Integer, Integer> totalWaterNeeded;

    /**
     * Constructor for the event dashboard
     */
    public EventDashboard() {
        setBorder(BorderFactory.createTitledBorder("Event Dashboard"));
        setLayout(new GridLayout(0, 1));
        eventProgressBars = new HashMap<>();
        totalWaterNeeded = new HashMap<>();
    }


    public void addFireEvent(int zoneId, int waterNeeded) {
        if (!eventProgressBars.containsKey(zoneId)) {
            JProgressBar progressBar = new JProgressBar(0, waterNeeded);
            progressBar.setStringPainted(true);
            progressBar.setString("0L / " + waterNeeded + "L");
            eventProgressBars.put(zoneId, progressBar);
            totalWaterNeeded.put(zoneId, waterNeeded);
            add(new JLabel("Fire at Zone " + zoneId + " - Remaining water needed to extinguish fire:"));
            add(progressBar);
            revalidate();
            repaint();
        }
    }

    public void updateFireProgress(int zoneId, int waterDropped) {
        JProgressBar progressBar = eventProgressBars.get(zoneId);
        if (progressBar != null) {
            int newProgress = progressBar.getValue() + waterDropped;
            newProgress = Math.min(newProgress, totalWaterNeeded.get(zoneId));
            progressBar.setValue(newProgress);
            progressBar.setString(newProgress + "L / " + totalWaterNeeded.get(zoneId) + "L");
        }
    }

    public void removeFireEvent(int zoneId) {
        if (eventProgressBars.containsKey(zoneId)) {
            remove(eventProgressBars.get(zoneId));
            eventProgressBars.remove(zoneId);
            totalWaterNeeded.remove(zoneId);
            revalidate();
            repaint();
        }
    }
}