package com.university.restaurant.model.staff;

public sealed interface StaffRole permits Manager, Waiter, Chef {
    String id();

    String name();
}
