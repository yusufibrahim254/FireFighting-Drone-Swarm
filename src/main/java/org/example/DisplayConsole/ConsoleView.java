package org.example.DisplayConsole;

import org.example.DroneSystem.*;
import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class ConsoleView extends JPanel{
    private final LinkedList<Zone> zones;
    private int GRID_WIDTH;
    private int GRID_HEIGHT;
    private static final int CELL_SIZE = 25;
    private static final int DRONE_SIZE = 25;
    private ConsoleController controller;
    private Image fireImage;
    private Image extinguishedImage;

    private Image droneImage;

    /**
     * Constructor for the console view
     * @param zones list of zones
     */
    public ConsoleView(LinkedList<Zone> zones, ConsoleController consoleController, int gridWidth, int gridHeight){
        this.GRID_WIDTH = gridWidth;
        this.GRID_HEIGHT = gridHeight;
        this.zones = zones;
        this.controller = consoleController;
        setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
        revalidate();
        repaint();
        fireImage = new ImageIcon("docs/icons/fireImage.png").getImage();
        extinguishedImage = new ImageIcon("docs/icons/extinguished.png").getImage();
        droneImage = new ImageIcon("docs/icons/droneIcon.png").getImage();
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
        drawDrones(g);
    }



    public void drawFires(Graphics g) {
        Point offset = getGridOffset();
        int imgWidth = 55;
        int imgHeight = 65;

        for (Zone zone : zones) {
            int midX = offset.x + (zone.getZoneStart().getXCoords() + zone.getZoneEnd().getXCoords()) / 2;
            int midY = offset.y + (zone.getZoneStart().getYCoords() + zone.getZoneEnd().getYCoords()) / 2;
            int drawX = midX - imgWidth / 2;
            int drawY = midY - imgHeight / 2;

            if (controller.getFires().contains(zone.getZoneId())) {
                g.setColor(Color.RED);
                g.fillRect(midX, midY, 25, 25);
                g.drawImage(fireImage, drawX, drawY, imgWidth, imgHeight, null);
            } else if (controller.getExtinguishedFires().contains(zone.getZoneId())){
                g.setColor(Color.GREEN);
                g.fillRect(midX, midY, 25, 25);
                g.drawImage(extinguishedImage, drawX, drawY, 85, 75, null);
            }
        }
    }



    public void drawDrones(Graphics g) {
        Point offset = getGridOffset();
        g.setColor(Color.BLUE);
        Map<Integer, Point> dronePositions = controller.getDronePositions();
        Map<Integer, DroneState> droneStates = controller.getDroneStates();

        for (Map.Entry<Integer, Point> entry : dronePositions.entrySet()) {
            int droneId = entry.getKey();
            Point p = entry.getValue();
            int drawx = offset.x + p.x;
            int drawy = offset.y + p.y;

            DroneState state = droneStates.get(droneId);

            getDroneColor(state, g);

//            g.fillRect(drawx, drawy, DRONE_SIZE, DRONE_SIZE);
            g.drawImage(getTintedDroneImage(state, g), drawx, drawy, DRONE_SIZE, DRONE_SIZE, this);
            g.setColor(Color.BLACK);
            g.drawString("D" + entry.getKey(), drawx + 3 , drawy + 20);
        }
    }

    private Color getDroneColor(DroneState state, Graphics g){
        if (state instanceof EnRouteState){
            g.setColor(Color.BLUE);
            return Color.BLUE;
        } else if (state instanceof ReturningState){
            g.setColor(Color.YELLOW);
            return Color.YELLOW;
        } else if (state instanceof DroppingAgentState) {
            g.setColor(Color.MAGENTA);
            return Color.MAGENTA;
        } else if (state instanceof FaultedState){
            g.setColor(Color.BLACK);
            return Color.BLACK;
        } else {
            g.setColor(Color.GRAY);
            return Color.GRAY;
        }
    }

    private Image getTintedDroneImage(DroneState state, Graphics g) {
        Color tint = getDroneColor(state, g); // Get corresponding color
        BufferedImage tintedImage = new BufferedImage(droneImage.getWidth(null), droneImage.getHeight(null), BufferedImage.TRANSLUCENT);
        Graphics2D g2d = tintedImage.createGraphics();

        g2d.drawImage(droneImage, 0, 0, null);

        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(tint);
        g2d.fillRect(0, 0, tintedImage.getWidth(), tintedImage.getHeight());

        g2d.dispose();
        return tintedImage;
    }


    // refactor this so that they call the controller instead of the view to controller
    public void markFire(int zoneId) {
        controller.markFire(zoneId);
    }


    public void clearFireInZone(int zoneId) {
        controller.clearFireInZone(zoneId);
    }

    public void updateDroneState(int droneId, DroneState state) {
        controller.updateDroneState(droneId, state);
    }

    public void updateDronePosition(int droneId, int x, int y) {
        controller.updateDronePosition(droneId, x, y);
    }

    public ConsoleController getController() {
        return controller;
    }

    public void setController(ConsoleController controller) {
        this.controller = controller;
    }
}
