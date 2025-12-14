package org.example.domain;
import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


// PERSISTENCE BOUNDARY (Repositories)

interface MenuRepository {
    Optional<MenuItem> findById(String id);
    List<MenuItem> findByCategory(MenuCategory category);
    List<MenuItem> search(Predicate<MenuItem> filter);
    void save(MenuItem item);
}

interface OrderRepository {
    Optional<Order> findById(UUID id);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByTable(int tableNumber);
    void save(Order order);
}

interface ReservationRepository {
    Optional<Reservation> findById(UUID id);
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findActive();
    void save(Reservation reservation);
}

interface InventoryRepository {
    Optional<InventoryItem> findById(String id);
    Optional<InventoryItem> findByName(String name);
    List<InventoryItem> findByStatus(StockStatus status);
    void save(InventoryItem item);
}

interface RestaurantAuditLogRepository {
    void append(RestaurantAuditEntry entry);
    List<RestaurantAuditEntry> all();
    boolean verifyChain();
    String tailHash();
}

//---------------------------------------------------------------------
// In-memory implementations
//---------------------------------------------------------------------

final class InMemoryMenuRepo implements MenuRepository {
    private final Map<String, MenuItem> store = new HashMap<>();

    @Override
    public Optional<MenuItem> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MenuItem> findByCategory(MenuCategory category) {
        return store.values().stream()
                .filter(item -> item.category == category)
                .toList();
    }

    @Override
    public List<MenuItem> search(Predicate<MenuItem> filter) {
        return store.values().stream().filter(filter).toList();
    }

    @Override
    public void save(MenuItem item) {
        store.put(item.id, item);
    }
}

final class InMemoryOrderRepo implements OrderRepository {
    private final Map<UUID, Order> store = new HashMap<>();

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    @Override
    public List<Order> findByTable(int tableNumber) {
        return store.values().stream()
                .filter(order -> order.getTableNumber() == tableNumber)
                .toList();
    }

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }
}

final class InMemoryReservationRepo implements ReservationRepository {
    private final Map<UUID, Reservation> store = new HashMap<>();

    @Override
    public Optional<Reservation> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        return store.values().stream()
                .filter(r -> r.getReservationTime().toLocalDate().equals(date))
                .toList();
    }

    @Override
    public List<Reservation> findActive() {
        return store.values().stream()
                .filter(Reservation::isActive)
                .toList();
    }

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
    }
}

final class InMemoryInventoryRepo implements InventoryRepository {
    private final Map<String, InventoryItem> store = new HashMap<>();

    @Override
    public Optional<InventoryItem> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<InventoryItem> findByName(String name) {
        return store.values().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<InventoryItem> findByStatus(StockStatus status) {
        return store.values().stream()
                .filter(item -> item.getStatus() == status)
                .toList();
    }

    @Override
    public void save(InventoryItem item) {
        store.put(item.getId(), item);
    }
}

final class RestaurantAuditEntry {
    final String userId, role, action, entityType, entityId, details, prevHash, hash;
    final LocalDateTime timestamp = LocalDateTime.now();

    RestaurantAuditEntry(String userId, String role, String action,
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

final class InMemoryRestaurantAuditRepo implements RestaurantAuditLogRepository {
    private final List<RestaurantAuditEntry> log = new ArrayList<>();

    @Override
    public void append(RestaurantAuditEntry entry) { log.add(entry); }

    @Override
    public List<RestaurantAuditEntry> all() { return List.copyOf(log); }

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
