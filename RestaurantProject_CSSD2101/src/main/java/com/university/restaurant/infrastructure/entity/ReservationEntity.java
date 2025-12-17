package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.reservation.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for Reservations.
 * Contains embedded customer information.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Embedded
    private CustomerEntity customer;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "party_size", nullable = false)
    private Integer partySize;

    @Column(name = "assigned_table")
    private Integer assignedTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    // Default constructor for JPA
    public ReservationEntity() {}

    // Constructor
    public ReservationEntity(UUID id, CustomerEntity customer, LocalDateTime reservationTime,
                             Integer partySize, ReservationStatus status) {
        this.id = id;
        this.customer = customer;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.status = status;
        this.assignedTable = -1;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public Integer getAssignedTable() {
        return assignedTable;
    }

    public void setAssignedTable(Integer assignedTable) {
        this.assignedTable = assignedTable;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
