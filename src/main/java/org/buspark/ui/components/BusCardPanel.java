package org.buspark.ui.components;

import org.buspark.model.Bus;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class BusCardPanel extends JPanel {
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    public BusCardPanel(Bus bus, int index, boolean isRoute) {
        setLayout(new BorderLayout(15, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 238, 242), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        setOpaque(false);

        Color accent = isRoute ? new Color(16, 185, 129) : new Color(71, 85, 105);

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel idx = new JLabel("#" + index);
        idx.setForeground(accent);
        idx.setFont(new Font("Arial", Font.BOLD, 11));
        JLabel plate = new JLabel(bus.getBusNumber());
        plate.setFont(new Font("Arial", Font.BOLD, 20));
        left.add(idx); left.add(plate);

        JLabel route = new JLabel("R-" + bus.getRouteNumber(), SwingConstants.RIGHT);
        route.setFont(new Font("Arial", Font.BOLD, 22));
        route.setForeground(accent);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 5));
        center.setOpaque(false);
        JLabel driver = new JLabel(bus.getFirstName() + " " + bus.getLastName());
        driver.setFont(new Font("Arial", Font.PLAIN, 15));

        String timeStr = bus.getDepartureTime().format(TF) + " - " + bus.getArrivalTime().format(TF);
        JLabel time = new JLabel("⏰ " + timeStr);
        time.setForeground(new Color(100, 116, 139));
        time.setFont(new Font("Monospaced", Font.BOLD, 13));

        center.add(driver);
        center.add(time);

        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(route, BorderLayout.EAST);

        setMaximumSize(new Dimension(600, 110));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        g2.dispose();
    }
}