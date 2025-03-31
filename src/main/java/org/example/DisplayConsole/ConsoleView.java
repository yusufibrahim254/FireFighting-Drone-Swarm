package org.example.DisplayConsole;

import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class ConsoleView extends JPanel{
    private LinkedList<Zone> zones;

    public ConsoleView(LinkedList<Zone> zones){
        this.zones = zones;
        setPreferredSize(new Dimension(500, 500));
    }

    public void drawGrid(Graphics g){
        g.setColor(Color.GRAY);
        for (int i = 0; i <= 400; i+= 25){
            g.drawLine(i, 0, i, 400);
            g.drawLine(0, i, 400, i);
        }
    }

    public void drawZones(Graphics g){
        g.setColor(Color.BLACK);
        for (Zone zone: zones){
            System.out.println(zone);
            g.drawRect(zone.getZoneStart().getX(), zone.getZoneStart().getY(), zone.getWidth(), zone.getLength());
            g.drawString("Z(" + zone.getZoneId() + ")", zone.getZoneStart().getX() + 5, zone.getZoneStart().getY() + 15);
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawGrid(g);
        drawZones(g);
    }
}
