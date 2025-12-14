package org.example.domain;

import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

enum OrderStatus {PENDING, CONFIRMED, PREPARED, READY, SERVED, PAID, CANCELLED}
enum PaymentMethod {CASH, CREDIT_CARD, DEBIT_CARD, MOBILE}

final class Payment{
    private final PaymentMethod method;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String transactionId;

    Payment(PaymentMethod method, double amount){
        this.method = method;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0,8);
    }

    @Override
    public String toString(){
        return "%s: %.2f [%s]".formatted(method, amount, transactionId);
    }
}

final class Order {
    private final UUID id;
    private final List<MenuItem> items;
    private final int tableNumber;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private Payment payment;
    private String assignedWaiterId;

    Order(int tableNumber, String waiterId){
        this.id = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.tableNumber = tableNumber;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.assignedWaiterId = waiterId;
    }

    void addItem(MenuItem item) {
        if (!item.available)
            throw new IllegalStateException("Item not available: " + item.name);
        items.add(item);
    }

    double calculateTotal(){
        return items.stream().mapToDouble(MenuItem::calculatePrice).sum();
    }

    void updateStatus(OrderStatus newStatus){
        this.status = newStatus;
    }

    void processPayment(PaymentMethod method){
        if(status != OrderStatus.SERVED)
            throw new IllegalArgumentException("Order must be served before payment");

        this.payment = new Payment(method, calculateTotal());
        this.status = OrderStatus.PAID;
    }

    boolean requiresKitchenPrep(){
        return items.stream().anyMatch(MenuItem::requiresKitchenPrep);
    }

    @Override
    public String toString(){
        return "Order[%s | Table=%d | Items=%d | Total=$%.2f | Status=%s]"
                .formatted(id.toString().substring(0, 8), tableNumber,
                        items.size(), calculateTotal(), status);
    }

    public UUID getId(){ return id;}
    public OrderStatus getStatus(){ return status;}
    public int getTableNumber(){ return tableNumber;}
    public List<MenuItem> getItems(){ return List.copyOf(items);}

}
