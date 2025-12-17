package com.university.restaurant.repository;

import com.university.restaurant.model.reservation.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Optional<Reservation> findById(UUID id);

    List<Reservation> findByDate(LocalDate date);

    List<Reservation> findActive();

    void save(Reservation reservation);
}
