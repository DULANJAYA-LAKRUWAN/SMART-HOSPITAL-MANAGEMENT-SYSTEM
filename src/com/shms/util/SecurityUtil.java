package com.shms.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enterprise Native Security Engine (Zero-Dependency)
 * Implements Salted Hashing using Java Standard Library.
 * Resolves all 'mindrot/BCrypt' errors.
 */
public class SecurityUtil {

    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hashes password using Native SHA-256 + Random Salt.
     * @return Format: salt:hashBase64
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);
            
            return saltB64 + ":" + hashB64;
        } catch (Exception e) {
            System.err.println("[SecurityUtil] Native Hash Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifies password against Native Salted Hash.
     */
    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null || !storedHash.contains(":")) return false;
        try {
            String[] parts = storedHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes());

            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }
}
