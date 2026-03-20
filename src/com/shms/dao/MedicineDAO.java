package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Medicine;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public List<String> getLowStockItems() {
        List<String> lowStock = new ArrayList<>();
        String query = "SELECT name FROM medicines WHERE stock_quantity < 10";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                lowStock.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error checking low stock: " + e.getMessage());
        }
        return lowStock;
    }

    public Medicine findByBarcode(String barcode) {
        String query = "SELECT * FROM medicines WHERE barcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, barcode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Medicine m = new Medicine();
                    m.setMedicineId(rs.getInt("medicine_id"));
                    m.setName(rs.getString("name"));
                    m.setUnitPrice(rs.getDouble("unit_price"));
                    m.setStockQuantity(rs.getInt("stock_quantity"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error finding barcode: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStock(int medId, int qtyToSubtract) {
        String query = "UPDATE medicines SET stock_quantity = stock_quantity - ? WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, qtyToSubtract);
            pst.setInt(2, medId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error updating stock: " + e.getMessage());
            return false;
        }
    }
}
