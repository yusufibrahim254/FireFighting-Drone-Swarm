package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConsoleView extends JPanel{
    private final LinkedList<Zone> zones;
    private Set<Integer> fires = new HashSet<>();
    private Set<Integer> extinguishedFires = new HashSet<>();

    /**
     * Constructor for the console view
     * @param zones list of zones
     */
    public ConsoleView(LinkedList<Zone> zones){
        super();
        this.zones = zones;
        setPreferredSize(new Dimension(400, 400));
    }

    /**
     * Draws the grid on the GUI
     * @param g graphics
     */
    public void drawGrid(Graphics g){
        g.setColor(Color.GRAY);
        for (int i = 0; i <= 400; i+= 25){
            g.drawLine(i, 0, i, 400);
            g.drawLine(0, i, 400, i);
        }
    }

    /**
     * Draws the zones on the GUI
     * @param g graphics
     */
    public void drawZones(Graphics g){
        g.setColor(Color.BLACK);
        for (Zone zone: zones){
            g.drawRect(zone.getZoneStart().getXCoords(), zone.getZoneStart().getYCoords(), zone.getWidth(), zone.getLength());
            g.drawString("Z(" + zone.getZoneId() + ")", zone.getZoneStart().getXCoords() + 3, zone.getZoneStart().getYCoords() + 15);
        }
    }

    /**
     * Change the GUI construction to include the grid and zones
     * @param g the Graphics object to protect
     */
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawGrid(g);
        drawZones(g);
        drawFires(g);
    }

    public void markFire(int zoneId) {
        fires.add(zoneId);
        extinguishedFires.remove(zoneId);
        repaint();
    }


    public void clearFireInZone(int zoneId) {
        fires.remove(zoneId);
        extinguishedFires.add(zoneId);
        repaint();
    }

    public void drawFires(Graphics g) {

        for (Zone zone : zones) {
            int midX = (zone.getZoneStart().getXCoords() + zone.getZoneEnd().getXCoords()) / 2;
            int midY = (zone.getZoneStart().getYCoords() + zone.getZoneEnd().getYCoords()) / 2;
            if (fires.contains(zone.getZoneId())) {
                g.setColor(Color.RED);
                g.fillRect(midX, midY, 25, 25);
            } else if (extinguishedFires.contains(zone.getZoneId())){
                g.setColor(Color.GREEN);
                g.fillRect(midX, midY, 25, 25);
            }
        }
    }

    public Set<Integer> getFires() {
        return fires;
    }

    public void setFires(Set<Integer> fires) {
        this.fires = fires;
    }
}
