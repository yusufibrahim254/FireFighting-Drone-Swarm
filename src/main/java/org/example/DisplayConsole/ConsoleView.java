package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Coordinates;
import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConsoleView extends JPanel{
    private LinkedList<Zone> zones;
    private Set<Integer> fires = new HashSet<>();

    /**
     * Constructor for the console view
     * @param zones list of zones
     */
    public ConsoleView(LinkedList<Zone> zones){
        this.zones = zones;
        setPreferredSize(new Dimension(500, 500));
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
            System.out.println(zone);
            g.drawRect(zone.getZoneStart().getXCoords(), zone.getZoneStart().getYCoords(), zone.getWidth(), zone.getLength());
            g.drawString("Z(" + zone.getZoneId() + ")", zone.getZoneStart().getXCoords() + 5, zone.getZoneStart().getYCoords() + 15);
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
        repaint();
    }


    public void clearFireInZone(int zoneId) {
        fires.remove(zoneId);
        repaint();
    }

    public void drawFires(Graphics g) {
        g.setColor(Color.RED);
        for (Zone zone : zones) {
            if (fires.contains(zone.getZoneId())) {
                int midX = (zone.getZoneStart().getXCoords() + zone.getZoneEnd().getXCoords()) / 2;
                int midY = (zone.getZoneStart().getYCoords() + zone.getZoneEnd().getYCoords()) / 2;
                g.fillRect(midX, midY, 25, 25);
            }
        }
    }
}
