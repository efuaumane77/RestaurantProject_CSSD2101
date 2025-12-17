package com.university.restaurant.infrastructure.adapter.mapper;

import com.university.restaurant.infrastructure.entity.PaymentEntity;
import com.university.restaurant.model.payment.Payment;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Payment domain models and PaymentEntity JPA entities.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class PaymentMapper {

    /**
     * Convert domain Payment to JPA entity.
     */
    public PaymentEntity toEntity(Payment domain) {
        return new PaymentEntity(
                domain.getTransactionId(),
                domain.getMethod(),
                domain.getAmount(),
                domain.getTimestamp()
        );
    }

    /**
     * Convert JPA entity to domain Payment.
     */
    public Payment toDomain(PaymentEntity entity) {
        return new Payment(
                entity.getMethod(),
                entity.getAmount()
        );
    }
}
