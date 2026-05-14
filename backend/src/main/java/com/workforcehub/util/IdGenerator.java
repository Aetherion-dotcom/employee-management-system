package com.workforcehub.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating unique employee IDs and other identifiers.
 */
@Component
public class IdGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a unique employee ID with a given prefix.
     */
    public static String generateEmployeeId(String prefix, long sequence) {
        return String.format("%s%03d", prefix, sequence);
    }

    /**
     * Generate a random string of specified length.
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Generate a file name with timestamp to avoid collisions.
     */
    public static String generateFileName(String originalName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = generateRandomString(6);
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }
        return timestamp + "_" + randomSuffix + extension;
    }
}
