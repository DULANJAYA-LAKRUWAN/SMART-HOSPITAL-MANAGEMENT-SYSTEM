package com.shms.service;

import com.shms.dao.PatientDAO;
import com.shms.model.Patient;
import java.util.List;

/**
 * Service Layer for Patient Management.
 * Follows the 3-Tier Layered Architecture requirement.
 */
public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public boolean registerPatient(Patient p) {
        // Business Logic: Check if NIC is valid or already exists
        if (p.getNicNumber() == null || p.getNicNumber().isEmpty()) {
            return false;
        }
        return patientDAO.savePatient(p);
    }

    public boolean updatePatient(Patient p) {
        return patientDAO.updatePatient(p);
    }

    public boolean deletePatient(int id) {
        return patientDAO.deletePatient(id);
    }

    public List<Patient> searchPatients(String query) {
        return patientDAO.searchPatients(query);
    }
}
