package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.adapter.mapper.PaymentMapper;
import com.university.restaurant.infrastructure.jpa.PaymentJpaRepository;
import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA Adapter implementing PaymentRepository port.
 * Bridges Alice's PaymentRepository interface with Spring Data JPA.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class PaymentJpaAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepo;
    private final PaymentMapper mapper;

    public PaymentJpaAdapter(PaymentJpaRepository jpaRepo, PaymentMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public void save(Payment payment) {
        jpaRepo.save(mapper.toEntity(payment));
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return jpaRepo.findById(transactionId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findByDate(LocalDate date) {
        return jpaRepo.findByDate(date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
