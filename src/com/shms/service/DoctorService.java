package com.shms.service;

import com.shms.dao.DoctorDAO;
import com.shms.model.Doctor;
import java.util.List;

/**
 * Service Layer for Medical Staff Management.
 * Part of the 3-Tier Layered Architecture implementation.
 */
public class DoctorService {

    private final DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    public boolean registerDoctor(Doctor doc) {
        if (doc.getConsultationFee() < 0) {
            return false;
        }
        return doctorDAO.saveDoctor(doc);
    }

    public boolean updateDoctor(Doctor doc) {
        return doctorDAO.updateDoctor(doc);
    }

    public boolean deleteDoctor(int id) {
        return doctorDAO.deleteDoctor(id);
    }

    public Doctor getDoctorById(int id) {
        return doctorDAO.getDoctorById(id);
    }
}
