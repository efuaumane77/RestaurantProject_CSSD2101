package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.model.payment.PaymentMethod;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Payment responses.
 */
public class PaymentDTO {
    private String transactionId;
    private PaymentMethod method;
    private Double amount;
    private LocalDateTime timestamp;
    private UUID orderId;

    public PaymentDTO() {}

    public PaymentDTO(String transactionId, PaymentMethod method, Double amount,
                      LocalDateTime timestamp, UUID orderId) {
        this.transactionId = transactionId;
        this.method = method;
        this.amount = amount;
        this.timestamp = timestamp;
        this.orderId = orderId;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
}