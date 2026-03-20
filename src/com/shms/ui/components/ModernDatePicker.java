package com.shms.ui.components;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Modern lightweight Date Picker dialog.
 */
public class ModernDatePicker extends JDialog {
    private LocalDate selectedDate;
    private JPanel grid;
    private JLabel lblMonth;
    private YearMonth currentMonth;

    public ModernDatePicker(Frame parent) {
        super(parent, "Select Date", true);
        this.currentMonth = YearMonth.now();
        initializeUI();
    }

    private void initializeUI() {
        setSize(320, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 112, 243));
        lblMonth = new JLabel("", SwingConstants.CENTER);
        lblMonth.setForeground(Color.WHITE);
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JButton btnPrev = new JButton("<");
        btnPrev.addActionListener(e -> updateCalendar(-1));
        JButton btnNext = new JButton(">");
        btnNext.addActionListener(e -> updateCalendar(1));
        
        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonth, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(0, 7));
        grid.setBackground(Color.WHITE);
        add(grid, BorderLayout.CENTER);
        
        renderDays();
    }

    private void updateCalendar(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        renderDays();
    }

    private void renderDays() {
        grid.removeAll();
        lblMonth.setText(currentMonth.getMonth().name() + " " + currentMonth.getYear());

        String[] headers = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String h : headers) {
            JLabel l = new JLabel(h, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            grid.add(l);
        }

        LocalDate first = currentMonth.atDay(1);
        int dayOfWeek = first.getDayOfWeek().getValue() % 7;
        
        for (int i = 0; i < dayOfWeek; i++) grid.add(new JLabel(""));

        for (int i = 1; i <= currentMonth.lengthOfMonth(); i++) {
            final int day = i;
            JButton btnDay = new JButton(String.valueOf(i));
            btnDay.setBackground(Color.WHITE);
            btnDay.addActionListener(e -> {
                selectedDate = currentMonth.atDay(day);
                dispose();
            });
            grid.add(btnDay);
        }
        grid.revalidate();
        grid.repaint();
    }

    public LocalDate pick() {
        setVisible(true);
        return selectedDate;
    }
}
