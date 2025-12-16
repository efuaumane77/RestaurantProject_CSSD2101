package com.university.restaurant.repository;

import com.university.restaurant.model.payment.Payment;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryPaymentRepo implements PaymentRepository {

    private final Map<String, Payment> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        storage.put(payment.getTransactionId(), payment);
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return Optional.ofNullable(storage.get(transactionId));
    }

    @Override
    public List<Payment> findByDate(LocalDate date) {
        return storage.values().stream()
                .filter(p -> p.getTimestamp().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(storage.values());
    }
}
