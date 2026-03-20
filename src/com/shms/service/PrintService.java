package com.shms.service;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enterprise Printing Engine for SHMS.
 * Formats data for standard 58mm/80mm Thermal Receipt Printers.
 * Phase 8: Peripheral Device Integration.
 */
public class PrintService implements Printable {

    private String content;

    /**
     * Prints a professional pharmacy/billing receipt.
     * @param receiptBody Prepared string content for the receipt.
     */
    public void printReceipt(String receiptBody) {
        this.content = receiptBody;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                System.err.println("[PrintService] Printing failed: " + e.getMessage());
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Header Style
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        int y = 20;
        
        // Split and draw rows
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("---")) {
                g.setFont(new Font("Monospaced", Font.PLAIN, 10));
            } else if (line.contains("TOTAL")) {
                g.setFont(new Font("Monospaced", Font.BOLD, 12));
            } else {
                g.setFont(new Font("Monospaced", Font.PLAIN, 10));
            }
            g.drawString(line, 10, y);
            y += 15;
        }

        return PAGE_EXISTS;
    }

    /**
     * Utility to generate a standardized receipt header.
     */
    public static String getReceiptHeader() {
        return "      SMART HOSPITAL (SHMS)      \n" +
               "     COLOMBO, SRI LANKA          \n" +
               "---------------------------------\n" +
               "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
               "---------------------------------\n";
    }
}
