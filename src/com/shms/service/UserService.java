package com.shms.service;

import com.shms.dao.UserDAO;
import com.shms.model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password);
    }

    public boolean isAuthorized(User user, String requiredRole) {
        if (user == null || user.getRole() == null) return false;
        return user.getRole().equalsIgnoreCase(requiredRole) || user.getRole().equalsIgnoreCase("ADMIN");
    }
}
