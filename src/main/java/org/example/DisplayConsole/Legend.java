package org.example.DisplayConsole;

import javax.swing.*;
import java.awt.*;

public class Legend extends JPanel {

    public Legend() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Legend"));

        add(createLegendItem(Color.RED, "Active Fire"));
        add(createLegendItem(Color.GREEN, "Extinguished Fire"));
        add(createLegendItem(Color.ORANGE, "Drone outbound | Returning"));
        add(createLegendItem(Color.BLUE, "Drone inbound | En route"));
        add(createLegendItem(Color.MAGENTA, "Drone extinguishing fire")); // template for later
        add(createLegendItem(Color.BLACK, "Drone faulted"));
        add(createLegendItem(Color.GRAY, "No Events | Idle"));

    }


    private JPanel createLegendItem(Color color, String text) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel colorBox = new JLabel();
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(text);

        itemPanel.add(colorBox);
        itemPanel.add(label);

        return itemPanel;
    }
}
