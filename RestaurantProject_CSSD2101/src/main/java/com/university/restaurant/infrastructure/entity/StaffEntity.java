package com.university.restaurant.infrastructure.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for Staff members.
 * Stores information about Managers, Waiters, and Chefs.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "staff")
public class StaffEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // "MANAGER", "WAITER", "CHEF"

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // Default constructor for JPA
    public StaffEntity() {}

    // Constructor
    public StaffEntity(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.active = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}