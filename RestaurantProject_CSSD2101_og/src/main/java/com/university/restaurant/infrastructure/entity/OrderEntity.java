package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.order.OrderStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for Orders.
 * An order contains multiple order items and is associated with a payment.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(name = "assigned_waiter_id", length = 50)
    private String assignedWaiterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "total_amount")
    private Double totalAmount;

    // One order has many order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    // One order has one payment (nullable - payment happens later)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentEntity payment;

    // Default constructor for JPA
    public OrderEntity() {}

    // Constructor
    public OrderEntity(UUID id, Integer tableNumber, String assignedWaiterId,
                       OrderStatus status, OffsetDateTime createdAt) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.assignedWaiterId = assignedWaiterId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Helper method to add item
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getAssignedWaiterId() {
        return assignedWaiterId;
    }

    public void setAssignedWaiterId(String assignedWaiterId) {
        this.assignedWaiterId = assignedWaiterId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }
}