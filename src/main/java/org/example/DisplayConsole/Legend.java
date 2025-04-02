package org.example.DisplayConsole;

import org.example.DroneSystem.DroneState;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Legend extends JPanel {
    private final ImageIcon droneIcon;
    private final ImageIcon fireIcon;
    private final ImageIcon extinguishedIcon;
    private final int imageSize = 20; // Adjust image size as needed

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
        add(createLegendItem(Color.BLACK, "Drone faulted"));
        add(createLegendItem(Color.GRAY, "No Events | Idle"));
    }

    private JPanel createLegendItem(ImageIcon icon, String text) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(text);

        itemPanel.add(iconLabel);
        itemPanel.add(textLabel);
        return itemPanel;
    }

    private JPanel createLegendItem(Color tint, String text) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel droneIconLabel = new JLabel();
        droneIconLabel.setIcon(new ImageIcon(getTintedDroneImage(tint)));

        JLabel textLabel = new JLabel(text);

        itemPanel.add(droneIconLabel);
        itemPanel.add(textLabel);
        return itemPanel;
    }

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

    private Image resizeImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
    }
}
