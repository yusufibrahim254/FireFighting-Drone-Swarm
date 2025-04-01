package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConsoleView extends JPanel{
    private final LinkedList<Zone> zones;
    private Set<Integer> fires = new HashSet<>();
    private Set<Integer> extinguishedFires = new HashSet<>();
    private static final int GRID_WIDTH = 400;
    private static final int GRID_HEIGHT = 400;
    private static final int CELL_SIZE = 25;

    /**
     * Constructor for the console view
     * @param zones list of zones
     */
    public ConsoleView(LinkedList<Zone> zones){
        this.zones = zones;
        setPreferredSize(new Dimension(500, 500));
    }

    private Point getGridOffset() {
        int offsetX = (getWidth() - GRID_WIDTH) / 2;
        int offsetY = (getHeight() - GRID_HEIGHT) / 2;
        return new Point(offsetX, offsetY);
    }

    /**
     * Draws the grid on the GUI
     * @param g graphics
     */
    public void drawGrid(Graphics g){
        Point offset = getGridOffset();
        g.setColor(Color.GRAY);
        for (int i = 0; i <= GRID_WIDTH; i+= CELL_SIZE){
            g.drawLine(offset.x + i, offset.y, offset.x + i, offset.y + GRID_HEIGHT);
            g.drawLine(offset.x, offset.y + i, offset.x + GRID_WIDTH, offset.y + i);
        }
    }

    /**
     * Draws the zones on the GUI
     * @param g graphics
     */
    public void drawZones(Graphics g){
        Point offset = getGridOffset();
        g.setColor(Color.BLACK);
        for (Zone zone: zones){
            int x = offset.x + zone.getZoneStart().getXCoords();
            int y = offset.y + zone.getZoneStart().getYCoords();
            g.drawRect(x, y, zone.getWidth(), zone.getLength());
            g.drawString("Z(" + zone.getZoneId() + ")", x + 5, y + 15);
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
        Point offset = getGridOffset();
        for (Zone zone : zones) {
            int midX = offset.x + (zone.getZoneStart().getXCoords() + zone.getZoneEnd().getXCoords()) / 2;
            int midY = offset.y + (zone.getZoneStart().getYCoords() + zone.getZoneEnd().getYCoords()) / 2;
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
