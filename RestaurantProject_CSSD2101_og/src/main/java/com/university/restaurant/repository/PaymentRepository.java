package com.university.restaurant.repository;

import com.university.restaurant.model.payment.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    /**
     * Save a payment using its transactionId as the key.
     */
    void save(Payment payment);

    /**
     * Look up a payment by its transactionId.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * List all payments made on a given date.
     */
    List<Payment> findByDate(LocalDate date);

    /**
     * Load all payments.
     */
    List<Payment> findAll();
}
