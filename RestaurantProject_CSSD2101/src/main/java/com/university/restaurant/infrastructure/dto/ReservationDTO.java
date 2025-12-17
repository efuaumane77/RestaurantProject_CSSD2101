package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.model.reservation.ReservationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Reservation responses.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class ReservationDTO {
    private UUID id;
    private CustomerDTO customer;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private Integer assignedTable;
    private ReservationStatus status;

    public ReservationDTO() {}

    public ReservationDTO(UUID id, CustomerDTO customer, LocalDateTime reservationTime,
                          Integer partySize, Integer assignedTable, ReservationStatus status) {
        this.id = id;
        this.customer = customer;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.assignedTable = assignedTable;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }

    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) { this.reservationTime = reservationTime; }

    public Integer getPartySize() { return partySize; }
    public void setPartySize(Integer partySize) { this.partySize = partySize; }

    public Integer getAssignedTable() { return assignedTable; }
    public void setAssignedTable(Integer assignedTable) { this.assignedTable = assignedTable; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}

/**
 * DTO for Customer information.
 */
class CustomerDTO {
    private String name;
    private String phone;
    private String email;

    public CustomerDTO() {}

    public CustomerDTO(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

