package com.shms.service;

import com.shms.dao.AuditDAO;
import com.shms.model.User;

/**
 * System Auditing Service: The entry point for all security and operational logs.
 */
public class AuditService {
    
    private final AuditDAO auditDAO = new AuditDAO();

    public void logAction(User user, String action, String module) {
        if (user != null) {
            auditDAO.log(user.getUserId(), action, module);
        }
    }

    public void logAnonymous(String action, String module) {
        auditDAO.log(0, action, module); // 0 = System/Anonymous
    }
}
