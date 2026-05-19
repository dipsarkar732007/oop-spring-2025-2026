package gui;

import entity.Bus;
import fileio.BusFileIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BusGUI extends JFrame {

    // ==========================================
    // TEXT FIELDS & COMPONENTS
    // ==========================================
    private JTextField idField, routeField, seatsField, fareField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    private BusFileIO fileIO = new BusFileIO();
    private Bus[] buses = new Bus[1000];
    private int busCount = 0;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public BusGUI() {
        // Frame Settings
        setTitle("Bus Ticket Management System");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Read initial data from file
        busCount = fileIO.readFromFile(buses);

        // Theme Colors
        Color darkBlue = new Color(16, 44, 87);
        Color lightBlue = new Color(230, 240, 255);
        Color white = Color.WHITE;

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(darkBlue);
        headerPanel.setPreferredSize(new Dimension(100, 70));

        JLabel titleLabel = new JLabel("BUS TICKET MANAGEMENT SYSTEM");
        titleLabel.setForeground(white);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Input Form Panel (Left Side)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(12, 1, 10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Bus Information"));
        leftPanel.setPreferredSize(new Dimension(350, 100));
        leftPanel.setBackground(lightBlue);

        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font fieldFont = new Font("Arial", Font.PLAIN, 15);

        // Adding Form Fields
        JLabel idLabel = new JLabel("Bus ID (8 Digits)");
        idLabel.setFont(labelFont);
        idField = new JTextField();
        idField.setFont(fieldFont);
        leftPanel.add(idLabel);
        leftPanel.add(idField);

        JLabel routeLabel = new JLabel("Route Name");
        routeLabel.setFont(labelFont);
        routeField = new JTextField();
        routeField.setFont(fieldFont);
        leftPanel.add(routeLabel);
        leftPanel.add(routeField);

        JLabel seatsLabel = new JLabel("Total Seats");
        seatsLabel.setFont(labelFont);
        seatsField = new JTextField();
        seatsField.setFont(fieldFont);
        leftPanel.add(seatsLabel);
        leftPanel.add(seatsField);

        JLabel fareLabel = new JLabel("Fare per Seat");
        fareLabel.setFont(labelFont);
        fareField = new JTextField();
        fareField.setFont(fieldFont);
        leftPanel.add(fareLabel);
        leftPanel.add(fareField);

        JLabel searchLabel = new JLabel("Search by ID or Route");
        searchLabel.setFont(labelFont);
        searchField = new JTextField();
        searchField.setFont(fieldFont);
        leftPanel.add(searchLabel);
        leftPanel.add(searchField);

        add(leftPanel, BorderLayout.WEST);

        // Data Table Panel (Center)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(white);

        String[] columns = { "Bus ID", "Route Name", "Total Seats", "Fare (BDT)" };

        // Disable cell editing directly from the table
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Button Panel (Bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(darkBlue);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton searchBtn = new JButton("Search");
        JButton viewBtn = new JButton("View All");
        JButton clearBtn = new JButton("Clear");

        JButton[] buttons = { addBtn, updateBtn, deleteBtn, searchBtn, viewBtn, clearBtn };
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Arial", Font.BOLD, 15));
            buttons[i].setFocusPainted(false);
            buttons[i].setPreferredSize(new Dimension(130, 40));
            buttonPanel.add(buttons[i]);
        }
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Action Listeners
        addBtn.addActionListener(e -> addBus());
        updateBtn.addActionListener(e -> updateBus());
        deleteBtn.addActionListener(e -> deleteBus());
        searchBtn.addActionListener(e -> searchBus());
        viewBtn.addActionListener(e -> viewAll());
        clearBtn.addActionListener(e -> clearFields());

        // Table Click Event (Populates fields when a row is selected)
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                idField.setText(tableModel.getValueAt(row, 0).toString());
                routeField.setText(tableModel.getValueAt(row, 1).toString());
                seatsField.setText(tableModel.getValueAt(row, 2).toString());
                fareField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        viewAll();
        setVisible(true);
    }

    // ==========================================
    // VALIDATION
    // ==========================================
    private boolean validateFields() {
        String id = idField.getText().trim();
        String route = routeField.getText().trim();
        String seats = seatsField.getText().trim();
        String fare = fareField.getText().trim();

        if (id.isEmpty() || route.isEmpty() || seats.isEmpty() || fare.isEmpty()) {
            showError("All fields are required!");
            return false;
        }
        if (!id.matches("\\d{8}")) {
            showError("Bus ID must be exactly 8 digits!");
            return false;
        }
        try {
            Integer.parseInt(seats);
        } catch (Exception ex) {
            showError("Total Seats must be numeric!");
            return false;
        }
        try {
            Double.parseDouble(fare);
        } catch (Exception ex) {
            showError("Fare must be numeric!");
            return false;
        }
        return true;
    }

    // ==========================================
    // CRUD OPERATIONS
    // ==========================================
    private void addBus() {
        if (!validateFields())
            return;

        String id = idField.getText().trim();
        for (int i = 0; i < busCount; i++) {
            if (buses[i].getId().equals(id)) {
                showError("Duplicate Bus ID!");
                return;
            }
        }

        buses[busCount++] = new Bus(
                idField.getText().trim(),
                routeField.getText().trim(),
                seatsField.getText().trim(),
                fareField.getText().trim());

        fileIO.writeToFile(buses, busCount);
        showInfo("Bus Added Successfully!");
        clearFields();
        viewAll();
    }

    private void updateBus() {
        if (!validateFields())
            return;

        String id = idField.getText().trim();
        boolean updated = false;

        for (int i = 0; i < busCount; i++) {
            if (buses[i].getId().equals(id)) {
                buses[i].setRouteName(routeField.getText().trim());
                buses[i].setTotalSeats(seatsField.getText().trim());
                buses[i].setFarePerSeat(fareField.getText().trim());
                updated = true;
                break;
            }
        }

        if (updated) {
            fileIO.writeToFile(buses, busCount);
            showInfo("Bus Updated Successfully!");
            clearFields();
            viewAll();
        } else {
            showError("Bus ID Not Found!");
        }
    }

    private void deleteBus() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showError("Enter Bus ID!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete this bus record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        Bus[] tempArray = new Bus[1000];
        int tempCount = 0;
        boolean deleted = false;

        for (int i = 0; i < busCount; i++) {
            if (!buses[i].getId().equals(id)) {
                tempArray[tempCount++] = buses[i];
            } else {
                deleted = true;
            }
        }

        if (deleted) {
            buses = tempArray;
            busCount = tempCount;
            fileIO.writeToFile(buses, busCount);
            showInfo("Bus Deleted Successfully!");
            clearFields();
            viewAll();
        } else {
            showError("Bus ID Not Found!");
        }
    }

    private void searchBus() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            showError("Enter ID or Route to Search!");
            return;
        }

        tableModel.setRowCount(0);
        for (int i = 0; i < busCount; i++) {
            if (buses[i].getId().contains(keyword) || buses[i].getRouteName().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[] { buses[i].getId(), buses[i].getRouteName(), buses[i].getTotalSeats(),
                        buses[i].getFarePerSeat() });
            }
        }
    }

    private void viewAll() {
        tableModel.setRowCount(0);
        for (int i = 0; i < busCount; i++) {
            tableModel.addRow(new Object[] { buses[i].getId(), buses[i].getRouteName(), buses[i].getTotalSeats(),
                    buses[i].getFarePerSeat() });
        }
    }

    private void clearFields() {
        idField.setText("");
        routeField.setText("");
        seatsField.setText("");
        fareField.setText("");
        searchField.setText("");
        table.clearSelection();
    }

    // ==========================================
    // DIALOG MESSAGES
    // ==========================================
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}