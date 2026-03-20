package com.shms.service;

import com.shms.dao.MedicineDAO;
import com.shms.model.Medicine;
import java.util.List;

/**
 * Service Layer for Retail Pharmacy & Inventory Management.
 * Implements barcode-based input handling logic.
 */
public class MedicineService {

    private final MedicineDAO medicineDAO;

    public MedicineService() {
        this.medicineDAO = new MedicineDAO();
    }

    public List<Medicine> getAllMedicines() {
        return medicineDAO.getAllMedicines();
    }

    public boolean registerMedicine(Medicine med) {
        return medicineDAO.saveMedicine(med);
    }

    public boolean updateMedicine(Medicine med) {
        return medicineDAO.updateMedicine(med);
    }

    public boolean deleteMedicine(int id) {
        return medicineDAO.deleteMedicine(id);
    }

    /**
     * Peripheral Device Logic: Barcode search integration.
     */
    public Medicine getMedicineByBarcode(String barcode) {
        return medicineDAO.getMedicineByBarcode(barcode);
    }

    /**
     * Inventory Logic: Stock subtraction upon billing.
     */
    public boolean updateStockBalance(int medicineId, int quantityToSubtract) {
        Medicine current = medicineDAO.getMedicineById(medicineId);
        if (current == null || current.getStockQuantity() < quantityToSubtract) {
            return false;
        }
        int newBalance = current.getStockQuantity() - quantityToSubtract;
        return medicineDAO.updateStock(medicineId, newBalance);
    }

    public List<Medicine> getLowStockAlerts(int threshold) {
        return medicineDAO.getLowStockMedicines(threshold);
    }
}
