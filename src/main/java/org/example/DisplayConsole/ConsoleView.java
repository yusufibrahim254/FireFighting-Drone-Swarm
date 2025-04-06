package org.example.DisplayConsole;

import org.example.DroneSystem.*;
import org.example.FireIncidentSubsystem.Helpers.Zone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;

/**
 * Represents the console interface, showcasing the active simulation of fires and firefighting drones
 */
public class ConsoleView extends JPanel{
    private final LinkedList<Zone> zones;
    private final Set<Integer> extinguishedTimersSet = new HashSet<>();
    private int GRID_WIDTH;
    private int GRID_HEIGHT;
    private static final int CELL_SIZE = 25;
    private static final int DRONE_SIZE = 25;
    private ConsoleController controller;
    private final Image fireImage;
    private final Image extinguishedImage;

    private final Image droneImage;

    /**
     * Constructor for the console view
     * @param zones list of zones
     */
    public ConsoleView(LinkedList<Zone> zones, ConsoleController consoleController){

        this.zones = zones;
        this.controller = consoleController;

        revalidate();
        repaint();
        fireImage = new ImageIcon("docs/icons/fireImage.png").getImage();
        extinguishedImage = new ImageIcon("docs/icons/extinguished.png").getImage();
        droneImage = new ImageIcon("docs/icons/droneIcon.png").getImage();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Display the coordinates that the mouse is hovering over
     * @param event the {@code MouseEvent} that initiated the
     *              {@code ToolTip} display
     * @return Coordinates of mouse
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        Point offset = getGridOffset();
        int x = event.getX() - offset.x;
        int y = event.getY() - offset.y;
        if (x >= 0 && x <= GRID_WIDTH && y >= 0 && y <= GRID_HEIGHT) {
            return String.format("Coords: (%d, %d)", x, y);
        }

        return null;
    }

    public void setGrid(int gridWidth, int gridHeight){
        this.GRID_WIDTH = gridWidth;
        this.GRID_HEIGHT = gridHeight;
        setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
        revalidate();
        repaint();
    }

    /**
     * Get the offset for the grid
     * @return the grid offset
     */
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


    /**
     * Draw the fires on the GUI
     * @param g graphics
     */
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
                g.fillRect(midX, midY, 25, 25);
                g.drawImage(extinguishedImage, drawX - 135, drawY - 120, 185, 175, null);

                int zoneId = zone.getZoneId();

                if (!extinguishedTimersSet.contains(zoneId)) {
                    extinguishedTimersSet.add(zoneId);

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            controller.getExtinguishedFires().remove(zoneId);
                            extinguishedTimersSet.remove(zoneId); // allow it to be reused again later
                            repaint();
                        }
                    }, 5000);
                }
            }
        }
    }


    /**
     * Draw the drones on the GUI
     * @param g graphics
     */
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
            g.drawString("D" + entry.getKey(), drawx + 10 , drawy + 35);
        }
    }

    /**
     * Get the color of the drone based on its state
     * @param state the state of the drone
     * @param g graphics
     * @return the color the drone should be
     */
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
            g.setColor(Color.RED);
            return Color.RED;
        } else {
            g.setColor(Color.GRAY);
            return Color.GRAY;
        }
    }

    /**
     * Get the color to tint the drone based on its state
     * @param state the state of the drone
     * @param g graphics
     * @return the tint the drone should have
     */
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

    /**
     * Mark a fire in a zone on the GUI
     * @param zoneId the zone id
     */
    public void markFire(int zoneId) {
        controller.markFire(zoneId);
    }

    /**
     * Clears the fire from a zone on the GUI
     * @param zoneId the zone id
     */
    public void clearFireInZone(int zoneId) {
        controller.clearFireInZone(zoneId);
    }

    /**
     * Updates the drone state on the GUI
     * @param droneId the id of the drone
     * @param state the state of the drone
     */
    public void updateDroneState(int droneId, DroneState state) {
        controller.updateDroneState(droneId, state);
    }

    /**
     * Updates the drone location on the GUI
     * @param droneId the id of the drone
     * @param x the x coords of the drone
     * @param y the y coords of the drone
     */
    public void updateDronePosition(int droneId, int x, int y) {
        controller.updateDronePosition(droneId, x, y);
    }

    /**
     * Gets the console controller
     * @return the controller
     */
    public ConsoleController getController() {
        return controller;
    }

    /**
     * Sets a new controller for the console
     * @param controller the controller
     */
    public void setController(ConsoleController controller) {
        this.controller = controller;
    }
}
