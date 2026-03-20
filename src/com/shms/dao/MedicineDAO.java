package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Medicine;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String query = "SELECT * FROM medicines ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error: " + e.getMessage());
        }
        return list;
    }

    public boolean saveMedicine(Medicine m) {
        String query = "INSERT INTO medicines (name, barcode, unit_price, stock_quantity, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, m.getName());
            pst.setString(2, m.getBarcode());
            pst.setDouble(3, m.getUnitPrice());
            pst.setInt(4, m.getStockQuantity());
            pst.setDate(5, Date.valueOf(m.getExpiryDate()));
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Save Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMedicine(Medicine m) {
        String query = "UPDATE medicines SET name=?, barcode=?, unit_price=?, stock_quantity=?, expiry_date=? WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, m.getName());
            pst.setString(2, m.getBarcode());
            pst.setDouble(3, m.getUnitPrice());
            pst.setInt(4, m.getStockQuantity());
            pst.setDate(5, Date.valueOf(m.getExpiryDate()));
            pst.setInt(6, m.getMedicineId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Update Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMedicine(int id) {
        String query = "DELETE FROM medicines WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Medicine getMedicineByBarcode(String barcode) {
        String query = "SELECT * FROM medicines WHERE barcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, barcode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error finding barcode: " + e.getMessage());
        }
        return null;
    }

    public Medicine getMedicineById(int id) {
        String query = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean updateStock(int medId, int newQty) {
        String query = "UPDATE medicines SET stock_quantity = ? WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, newQty);
            pst.setInt(2, medId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Medicine> getLowStockMedicines(int threshold) {
        List<Medicine> list = new ArrayList<>();
        String query = "SELECT * FROM medicines WHERE stock_quantity < ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, threshold);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            return list;
        }
        return list;
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setName(rs.getString("name"));
        m.setBarcode(rs.getString("barcode"));
        m.setUnitPrice(rs.getDouble("unit_price"));
        m.setStockQuantity(rs.getInt("stock_quantity"));
        if (rs.getDate("expiry_date") != null) {
            m.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        }
        return m;
    }
}
