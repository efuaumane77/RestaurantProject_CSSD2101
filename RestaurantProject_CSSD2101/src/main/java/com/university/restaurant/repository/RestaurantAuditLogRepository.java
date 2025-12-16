package com.university.restaurant.repository;

import java.util.List;

public interface RestaurantAuditLogRepository {
    void append(RestaurantAuditEntry entry);
    List<RestaurantAuditEntry> all();
    boolean verifyChain();
    String tailHash();
}
