package com.university.restaurant.model.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Payment{
    private final PaymentMethod method;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String transactionId;

    public Payment(PaymentMethod method, double amount){
        this.method = method;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0,8);
    }

    public double getAmount() {return amount;}

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString(){
        return "%s: %.2f [%s]".formatted(method, amount, transactionId);
    }
}
