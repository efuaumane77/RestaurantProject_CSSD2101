package com.university.restaurant.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public final class RestaurantAuditEntry {
    final String userId, role, action, entityType, entityId, details, prevHash, hash;
    final LocalDateTime timestamp = LocalDateTime.now();

    public RestaurantAuditEntry(String userId, String role, String action,
                         String entityType, String entityId, String details, String prevHash) {
        this.userId = userId;
        this.role = role;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.prevHash = prevHash;
        this.hash = sha256(userId + role + action + entityType + entityId + details + timestamp + prevHash);
    }

    static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "[%s | %s (%s) | %s:%s | %s | hash=%s]"
                .formatted(timestamp.toString().substring(11, 19),
                        userId, role, action, entityType, details,
                        hash.substring(0, 8));
    }
}
