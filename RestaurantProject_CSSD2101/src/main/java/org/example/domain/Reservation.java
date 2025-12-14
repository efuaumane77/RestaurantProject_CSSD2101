package org.example.domain;

import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

enum ReservationStatus {CONFIRMED, SEATED, COMPLETED, CANCELLED, NO_SHOW}

final class Customer{
    private final String id, name, phone, email;

    Customer(String id, String name, String phone, String email){
        this.id = id;
        this.name = name;
        this.phone = phone;;
        this.email = email;
    }

    @Override
    public String toString(){
        return "Customer[%s : %s | %s]".formatted(id, name, phone);
    }
    public String getId(){ return id;}
    public String getName(){return name;}
}

final class Reservation {
    private final UUID id;
    private final Customer customer;
    private final LocalDateTime reservationTime;
    private final int partySize;
    private int assignedTable;
    private ReservationStatus status;

    Reservation(Customer customer, LocalDateTime reservationTime, int partySize) {
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

    void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    boolean isActive() {
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

}
