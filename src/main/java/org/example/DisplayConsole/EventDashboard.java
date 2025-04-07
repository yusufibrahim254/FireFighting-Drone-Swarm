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
    private final Map<Integer, JLabel> eventLabels;

    /**
     * Constructor for the event dashboard
     */
    public EventDashboard() {
        setBorder(BorderFactory.createTitledBorder("Event Dashboard"));
        setLayout(new GridLayout(0, 1));
        eventProgressBars = new HashMap<>();
        totalWaterNeeded = new HashMap<>();
        eventLabels = new HashMap<>();
    }

    /**
     * Adds a fire event to the dashboard
     * @param zoneId the zone id of the ongoing fire
     * @param waterNeeded the amount of water needed to extinguish fire
     */
    public void addFireEvent(int zoneId, int waterNeeded) {
        // check if event already exists, otherwise add to the dashboard
        if (!eventProgressBars.containsKey(zoneId)) {
            JProgressBar progressBar = new JProgressBar(0, waterNeeded);
            progressBar.setStringPainted(true);
            progressBar.setString("0L / " + waterNeeded + "L");
            eventProgressBars.put(zoneId, progressBar);
            totalWaterNeeded.put(zoneId, waterNeeded);
            JLabel eventInfo = new JLabel("Fire at Zone " + zoneId + " - Remaining water needed to extinguish fire:");
            eventLabels.put(zoneId, eventInfo);
            add(eventInfo);
            add(progressBar);
            revalidate();
            repaint();
        }
    }

    /**
     * Update the progress of the fire event
     * @param zoneId the zone id of the fire
     * @param waterDropped the amount of water dropped on fire so far
     */
    public void updateFireProgress(int zoneId, int waterDropped) {
        JProgressBar progressBar = eventProgressBars.get(zoneId);
        JLabel eventLabel = eventLabels.get(zoneId);
        if (progressBar != null) { // event exists in dashboard, update it
            int newProgress = progressBar.getValue() + waterDropped;
            newProgress = Math.min(newProgress, totalWaterNeeded.get(zoneId));
            progressBar.setValue(newProgress);
            progressBar.setString(newProgress + "L / " + totalWaterNeeded.get(zoneId) + "L");
        }
    }

    /**
     * Removes fire event from the dashboard
     * @param zoneId the zone id
     */
    public void removeFireEvent(int zoneId) {
        // check if event exists in dashboard, then remove it
        if (eventProgressBars.containsKey(zoneId)) {
            remove(eventProgressBars.get(zoneId));
            remove(eventLabels.get(zoneId));
            eventProgressBars.remove(zoneId);
            totalWaterNeeded.remove(zoneId);
            eventLabels.remove(zoneId);

            revalidate();
            repaint();
        }
    }
}