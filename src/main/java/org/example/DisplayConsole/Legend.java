package org.example.DisplayConsole;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents the legend, explaining all icons in the console (drone icons, fire icons, etc.)
 */
public class Legend extends JPanel {
    private final ImageIcon droneIcon;
    private final ImageIcon fireIcon;
    private final ImageIcon extinguishedIcon;
    private final int imageSize = 20;

    /**
     * Constructor for Legend
     */
    public Legend() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Legend"));

        fireIcon = new ImageIcon(resizeImage("docs/icons/fireImage.png"));
        droneIcon = new ImageIcon(resizeImage("docs/icons/droneIcon.png"));
        extinguishedIcon = new ImageIcon(resizeImage("docs/icons/extinguished.png"));

        add(createLegendItem(fireIcon, "Active Fire"));
        add(createLegendItem(extinguishedIcon, "Extinguished Fire"));
        add(createLegendItem(Color.YELLOW, "Drone outbound | Returning"));
        add(createLegendItem(Color.BLUE, "Drone inbound | En route"));
        add(createLegendItem(Color.MAGENTA, "Drone extinguishing fire"));
        add(createLegendItem(Color.RED, "Drone faulted"));
        add(createLegendItem(Color.GRAY, "No Events | Idle"));
    }

    /**
     * Create an item in the legend that has a specific icon
     * @param icon icon of the item
     * @param description description of the item
     * @return the new item for the legend
     */
    public JPanel createLegendItem(ImageIcon icon, String description) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(description);

        itemPanel.add(iconLabel);
        itemPanel.add(textLabel);
        return itemPanel;
    }

    /**
     * Creates an item in the legend that has a specific color scheme applied to icon
     * @param tint tint for the item's icon
     * @param description description of the item
     * @return the new item for the legend
     */
    public JPanel createLegendItem(Color tint, String description) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel droneIconLabel = new JLabel();
        droneIconLabel.setIcon(new ImageIcon(getTintedDroneImage(tint)));

        JLabel textLabel = new JLabel(description);

        itemPanel.add(droneIconLabel);
        itemPanel.add(textLabel);
        return itemPanel;
    }

    /**
     * Get the tinted image of the drone
     * @param tint the tint to apply on drone image
     * @return the tinted drone image
     */
    private Image getTintedDroneImage(Color tint) {
        Image img = droneIcon.getImage();
        BufferedImage tintedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TRANSLUCENT);

        Graphics2D g2d = tintedImage.createGraphics();

        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(tint);
        g2d.fillRect(0, 0, imageSize, imageSize);
        g2d.dispose();

        return tintedImage;
    }

    /**
     * Resize the image
     * @param path location of the image
     * @return the resized image
     */
    public Image resizeImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
    }
}
