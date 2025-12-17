package com.university.restaurant.repository;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryRestaurantAuditRepo implements RestaurantAuditLogRepository {
    private final List<RestaurantAuditEntry> log = new ArrayList<>();

    @Override
    public void append(RestaurantAuditEntry entry) {
        log.add(entry);
    }

    @Override
    public List<RestaurantAuditEntry> all() {
        return List.copyOf(log);
    }

    @Override
    public boolean verifyChain() {
        for (int i = 1; i < log.size(); i++)
            if (!log.get(i).prevHash.equals(log.get(i - 1).hash))
                return false;
        return true;
    }

    @Override
    public String tailHash() {
        return log.isEmpty() ? "GENESIS" : log.get(log.size() - 1).hash;
    }
}
