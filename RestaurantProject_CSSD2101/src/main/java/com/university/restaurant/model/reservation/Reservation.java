package com.university.restaurant.model.reservation;

import java.time.*;
import java.util.*;

public final class Reservation {
    private final UUID id;
    private final Customer customer;
    private final LocalDateTime reservationTime;
    private final int partySize;
    private int assignedTable;
    private ReservationStatus status;

    public Reservation(Customer customer, LocalDateTime reservationTime, int partySize) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.status = ReservationStatus.CONFIRMED;
        this.assignedTable = -1;
    }

    void assignTable(int tableNumber) {
        this.assignedTable = tableNumber;
        this.status = ReservationStatus.SEATED;
    }

    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED || status == ReservationStatus.SEATED;
    }

    @Override
    public String toString() {
        return "Reservation[%s | %s | Party=%d | Table=%s | Time=%s | Status=%s]"
                .formatted(id.toString().substring(0, 8), customer.getName(),
                        partySize, assignedTable > 0 ? assignedTable : "Unassigned",
                        reservationTime, status);
    }

    public UUID getId() { return id; }
    public LocalDateTime getReservationTime() { return reservationTime; }
    public ReservationStatus getStatus() { return status; }
    public int getAssignedTable() { return assignedTable; }

    public Customer getCustomer() {
        return customer;
    }
    public int getPartySize() {
        return partySize;
    }

}
