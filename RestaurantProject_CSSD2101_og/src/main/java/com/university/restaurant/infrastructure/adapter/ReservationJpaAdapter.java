package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.adapter.mapper.ReservationMapper;
import com.university.restaurant.infrastructure.jpa.ReservationJpaRepository;
import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Adapter implementing ReservationRepository port.
 * Bridges Alice's ReservationRepository interface with Spring Data JPA.
 *
 * Note: Similar to OrderAdapter, has limitations due to domain model constraints.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class ReservationJpaAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepo;
    private final ReservationMapper mapper;

    public ReservationJpaAdapter(ReservationJpaRepository jpaRepo, ReservationMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        // Cannot fully convert entity back to domain
        throw new UnsupportedOperationException(
                "Reservation retrieval not supported due to domain model constraints."
        );
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        throw new UnsupportedOperationException(
                "Reservation retrieval not supported due to domain model constraints."
        );
    }

    @Override
    public List<Reservation> findActive() {
        throw new UnsupportedOperationException(
                "Reservation retrieval not supported due to domain model constraints."
        );
    }

    @Override
    public void save(Reservation reservation) {
        // Saving works
        jpaRepo.save(mapper.toEntity(reservation));
    }
}
