package com.logistics.server.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            return ENCODER.matches(rawPassword, encodedPassword);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isBcryptHash(String password) {
        return password != null && password.startsWith("$2a$")
                || password != null && password.startsWith("$2b$")
                || password != null && password.startsWith("$2y$");
    }
}