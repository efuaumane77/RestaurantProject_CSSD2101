package com.university.restaurant.repository;

import com.university.restaurant.model.reservation.Reservation;

import java.time.LocalDate;
import java.util.*;

public final class InMemoryReservationRepo implements ReservationRepository {
    private final Map<UUID, Reservation> store = new HashMap<>();

    @Override
    public Optional<Reservation> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        return store.values().stream()
                .filter(r -> r.getReservationTime().toLocalDate().equals(date))
                .toList();
    }

    @Override
    public List<Reservation> findActive() {
        return store.values().stream()
                .filter(Reservation::isActive)
                .toList();
    }

    @Override
    public void save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
    }
}
