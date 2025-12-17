package com.university.restaurant.model.reservation;

public final class Customer {
    private final String name, phone, email;

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer[%s : %s | %s]".formatted(name, phone, email);
    }

    public String getName() {
        return name;
    }
}
