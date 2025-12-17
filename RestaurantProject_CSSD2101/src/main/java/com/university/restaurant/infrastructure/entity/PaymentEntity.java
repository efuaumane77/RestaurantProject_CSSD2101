package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.payment.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for Payment records.
 * Each payment is associated with exactly one order.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @Column(name = "transaction_id", nullable = false, length = 50)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // One payment belongs to one order
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // Default constructor for JPA
    public PaymentEntity() {}

    // Constructor
    public PaymentEntity(String transactionId, PaymentMethod method, Double amount,
                         LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.method = method;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}