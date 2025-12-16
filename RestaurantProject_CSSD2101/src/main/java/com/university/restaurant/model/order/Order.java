package com.university.restaurant.model.order;

import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.menu.MenuItem;

import java.time.*;
import java.util.*;
import java.util.List;

public final class Order {
    private final UUID id;
    private final List<MenuItem> items;
    private final int tableNumber;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private Payment payment;
    private String assignedWaiterId;

    public Order(int tableNumber, String waiterId){
        this.id = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.tableNumber = tableNumber;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.assignedWaiterId = waiterId;
    }

    public void addItem(MenuItem item) {
        if (!item.isAvailable())
            throw new IllegalStateException("Item not available: " + item.getName());
        items.add(item);
    }

    public double calculateTotal(){
        return items.stream().mapToDouble(MenuItem::calculatePrice).sum();
    }

    public void updateStatus(OrderStatus newStatus){
        this.status = newStatus;
    }

    public void processPayment(PaymentMethod method){
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

    public OffsetDateTime getCreatedAt() {
        return createdAt.atOffset(ZoneOffset.UTC);
    }

    public Payment getPayment(){ return payment; }
}
