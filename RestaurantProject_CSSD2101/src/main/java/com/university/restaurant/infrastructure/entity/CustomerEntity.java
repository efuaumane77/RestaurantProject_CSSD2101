package com.university.restaurant.infrastructure.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for Customer information.
 * Embedded within ReservationEntity.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Embeddable
public class CustomerEntity {

    @Column(name = "customer_name", nullable = false, length = 100)
    private String name;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "customer_email", length = 100)
    private String email;

    // Default constructor for JPA
    public CustomerEntity() {}

    // Constructor
    public CustomerEntity(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
