package org.buspark.ui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.buspark.model.Bus;
import org.buspark.service.BusService;
import org.buspark.ui.components.BusCardPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private final BusService service = new BusService();
    private JPanel parkListContainer, routeListContainer;
    private JLabel parkCountLabel, routeCountLabel, timeLabel;
    private int lastCheckedMinute = -1;

    public MainFrame() {
        setupUI();
        startAutoTimer();
        refreshData();
    }

    private void setupUI() {
        setTitle("Central Dispatch Desktop Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout(0, 0));

        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        headerPanel.setPreferredSize(new Dimension(0, 180));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("CENTRAL DISPATCH SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(new Color(71, 85, 105));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 0, 10, 0);
        headerPanel.add(title, gbc);

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        leftSpacer.setPreferredSize(new Dimension(250, 10));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 1.0;
        headerPanel.add(leftSpacer, gbc);

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 60));
        timeLabel.setForeground(new Color(245, 158, 11));

        JPanel clockDarkPanel = new JPanel(new BorderLayout());
        clockDarkPanel.setBackground(new Color(15, 23, 42));
        clockDarkPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        clockDarkPanel.add(timeLabel);

        gbc.gridx = 1; gbc.weightx = 0;
        headerPanel.add(clockDarkPanel, gbc);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightButtons.setOpaque(false);
        rightButtons.setPreferredSize(new Dimension(250, 50));

        JButton importBtn = new JButton("IMPORT");
        importBtn.setFocusPainted(false);
        importBtn.addActionListener(e -> openFileChooser());

        JButton addBtn = new JButton("+ REGISTER");
        addBtn.setBackground(new Color(37, 99, 235));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.addActionListener(e -> showAddDialog());

        rightButtons.add(importBtn);
        rightButtons.add(addBtn);

        gbc.gridx = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 20);
        headerPanel.add(rightButtons, gbc);

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 40, 0));
        contentGrid.setOpaque(false);
        contentGrid.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        parkCountLabel = createBadge(new Color(148, 163, 184));
        routeCountLabel = createBadge(new Color(16, 185, 129));
        parkListContainer = createScrollableList();
        routeListContainer = createScrollableList();

        contentGrid.add(createColumnWrapper("DEPOT STORAGE", parkCountLabel, parkListContainer));
        contentGrid.add(createColumnWrapper("ACTIVE ROUTES", routeCountLabel, routeListContainer));
        add(contentGrid, BorderLayout.CENTER);
    }

    private JLabel createBadge(Color color) {
        JLabel l = new JLabel("0", SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        l.setOpaque(true);
        l.setBackground(color);
        l.setPreferredSize(new Dimension(45, 28));
        return l;
    }

    private JPanel createScrollableList() {
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(new Color(248, 250, 252));
        return list;
    }

    private JPanel createColumnWrapper(String title, JLabel countLbl, JPanel listPanel) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setOpaque(false);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(t, BorderLayout.WEST);
        header.add(countLbl, BorderLayout.EAST);
        wrapper.add(header, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshData() {
        updateCards(parkListContainer, service.getBusesInDepot(), parkCountLabel, false);
        updateCards(routeListContainer, service.getBusesOnRoute(), routeCountLabel, true);
    }

    private void updateCards(JPanel container, List<Bus> list, JLabel countLbl, boolean isRoute) {
        container.removeAll();
        int i = 1;
        for (Bus b : list) {
            container.add(new BusCardPanel(b, i++, isRoute));
            container.add(Box.createVerticalStrut(15));
        }
        container.add(Box.createVerticalGlue());
        countLbl.setText(String.valueOf(list.size()));
        container.revalidate();
        container.repaint();
    }

    private void startAutoTimer() {
        Timer timer = new Timer(1000, e -> {
            LocalTime now = LocalTime.now();
            timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (now.getMinute() != lastCheckedMinute) {
                lastCheckedMinute = now.getMinute();
                new Thread(() -> {
                    if (service.checkSchedules() > 0) SwingUtilities.invokeLater(this::refreshData);
                }).start();
            }
        });
        timer.start();
    }

    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Data", "csv", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            processFile(chooser.getSelectedFile());
        }
    }

    private void processFile(File file) {
        int added = 0;
        int duplicates = 0;
        int formatErrors = 0;
        int totalLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                totalLines++;
                String[] p = line.split(",");

                if (p.length == 6) {
                    try {
                        Bus b = new Bus(
                                p[0].trim(),
                                p[1].trim(),
                                p[2].trim(),
                                Integer.parseInt(p[3].trim()),
                                LocalTime.parse(p[4].trim()),
                                LocalTime.parse(p[5].trim())
                        );

                        if (service.registerBus(b)) {
                            added++;
                        } else {
                            duplicates++;
                        }
                    } catch (Exception e) {
                        formatErrors++;
                    }
                } else {
                    formatErrors++;
                }
            }

            refreshData();

            if (totalLines == 0) {
                JOptionPane.showMessageDialog(this, "The file is empty!", "Empty File", JOptionPane.WARNING_MESSAGE);
            }
            else if (added > 0) {
                StringBuilder msg = new StringBuilder("Successfully imported " + added + " buses.");
                if (duplicates > 0) msg.append("\n- Skipped ").append(duplicates).append(" duplicates.");
                if (formatErrors > 0) msg.append("\n- Failed to parse ").append(formatErrors).append(" lines.");

                JOptionPane.showMessageDialog(this, msg.toString(), "Import Completed", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                if (duplicates > 0 && formatErrors == 0) {
                    JOptionPane.showMessageDialog(this,
                            "No new buses added. All " + duplicates + " records already exist in the database.",
                            "Duplicates Found", JOptionPane.WARNING_MESSAGE);
                }
                else if (formatErrors > 0 && duplicates == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Import failed. Format of all " + formatErrors + " lines is invalid.\nCheck: Plate, Name, Surname, Route, HH:mm, HH:mm",
                            "Format Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(this,
                            "Import failed.\nDuplicates: " + duplicates + "\nInvalid format: " + formatErrors,
                            "Import Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not read file: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JDialog d = new JDialog(this, "Registration", true);
        d.setSize(450, 550);
        d.setLayout(new GridBagLayout());
        d.setLocationRelativeTo(this);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 20, 10, 20); c.fill = GridBagConstraints.HORIZONTAL;

        JTextField plateF = new JTextField();
        JTextField fNameF = new JTextField();
        JTextField lNameF = new JTextField();
        JTextField routeF = new JTextField();
        JSpinner depS = createTimeSpinner();
        JSpinner arrS = createTimeSpinner();
        JLabel err = new JLabel(" "); err.setForeground(Color.RED);

        addFormRow(d, "Plate:", plateF, c, 0);
        addFormRow(d, "First Name:", fNameF, c, 1);
        addFormRow(d, "Last Name:", lNameF, c, 2);
        addFormRow(d, "Route:", routeF, c, 3);
        addFormRow(d, "Departure:", depS, c, 4);
        addFormRow(d, "Arrival:", arrS, c, 5);
        c.gridy = 6; c.gridwidth = 2; d.add(err, c);

        JButton save = new JButton("SAVE TO DATABASE");
        save.setPreferredSize(new Dimension(0, 45));
        save.setBackground(new Color(37, 99, 235));
        save.setForeground(Color.WHITE);
        save.addActionListener(e -> {
            String plate = plateF.getText().trim().toUpperCase();
            if (!plate.matches("^[A-Z]{2}\\d{4}[A-Z]{2}$")) { err.setText("Invalid format!"); return; }
            try {
                LocalTime dep = toLocalTime((Date)depS.getValue());
                LocalTime arr = toLocalTime((Date)arrS.getValue());
                Bus b = new Bus(plate, fNameF.getText(), lNameF.getText(), Integer.parseInt(routeF.getText()), dep, arr);
                if (service.registerBus(b)) { refreshData(); d.dispose(); }
                else err.setText("Already exists!");
            } catch (Exception ex) { err.setText("Check inputs!"); }
        });
        c.gridy = 7; d.add(save, c);
        d.setVisible(true);
    }

    private JSpinner createTimeSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        s.setEditor(new JSpinner.DateEditor(s, "HH:mm"));
        return s;
    }

    private LocalTime toLocalTime(Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.MINUTES);
    }

    private void addFormRow(JDialog d, String l, JComponent comp, GridBagConstraints c, int y) {
        c.gridwidth = 1; c.gridx = 0; c.gridy = y; c.weightx = 0.3; d.add(new JLabel(l), c);
        c.gridx = 1; c.weightx = 0.7; d.add(comp, c);
    }

    public static void main(String[] args) {
        FlatMacLightLaf.setup();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}