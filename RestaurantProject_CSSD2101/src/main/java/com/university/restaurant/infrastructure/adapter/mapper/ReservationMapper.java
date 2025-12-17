package com.university.restaurant.infrastructure.adapter.mapper;

import com.university.restaurant.infrastructure.entity.CustomerEntity;
import com.university.restaurant.infrastructure.entity.ReservationEntity;
import com.university.restaurant.model.reservation.Customer;
import com.university.restaurant.model.reservation.Reservation;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Reservation domain models and ReservationEntity JPA entities.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class ReservationMapper {

    /**
     * Convert domain Customer to embedded CustomerEntity.
     */
    public CustomerEntity toCustomerEntity(Customer domain) {
        return new CustomerEntity(
                domain.getName(),
                domain.getPhone(),
                domain.getEmail()
        );
    }

    /**
     * Convert domain Reservation to JPA entity.
     */
    public ReservationEntity toEntity(Reservation domain) {
        CustomerEntity customerEntity = toCustomerEntity(domain.getCustomer());

        ReservationEntity entity = new ReservationEntity(
                domain.getId(),
                customerEntity,
                domain.getReservationTime(),
                domain.getPartySize(),
                domain.getStatus()
        );

        if (domain.getAssignedTable() > 0) {
            entity.setAssignedTable(domain.getAssignedTable());
        }

        return entity;
    }

    /**
     * Convert JPA entity to domain Reservation.
     */
    public Reservation toDomain(ReservationEntity entity) {
        CustomerEntity custEntity = entity.getCustomer();
        Customer customer = new Customer(
                custEntity.getName(),
                custEntity.getPhone(),
                custEntity.getEmail()
        );

        // Note: Domain Reservation generates UUID internally
        // We can't reconstruct it with the exact same UUID from the entity
        throw new UnsupportedOperationException(
                "Cannot fully convert ReservationEntity to Reservation domain model. " +
                        "Reservation domain class needs modification to support reconstruction."
        );
    }
}
