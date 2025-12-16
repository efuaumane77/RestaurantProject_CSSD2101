package com.university.restaurant.service;

import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.order.*;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.staff.*;
import com.university.restaurant.repository.InMemoryOrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    InMemoryOrderRepo repo;
    AnalyticsService service;

    Manager manager = new Manager("m1", "Alice Manager");
    Waiter waiter = new Waiter("w1", "Bob Waiter");
    Chef chef = new Chef("c1", "Charlie Chef");

    MenuItem pasta;
    MenuItem burger;
    MenuItem drink;

    @BeforeEach
    void setup() {
        repo = new InMemoryOrderRepo();
        service = new AnalyticsService(repo);

        pasta = new Entree("i1", "Pasta", "Fresh pasta", 12.0,
                DietaryType.REGULAR, List.of("flour", "sauce"), 10);

        burger = new Entree("i2", "Burger", "Beef burger", 15.0,
                DietaryType.REGULAR, List.of("beef", "bun"), 8);

        drink = new Drink("i3", "Cola", "Refreshing drink", 3.0, false);
    }

    // -------------------------------------------------------------
    // PERMISSION TESTS
    // -------------------------------------------------------------

    @Test
    void waiterCannotViewAnalytics() {
        assertThrows(SecurityException.class,
                () -> service.topSellingItems(waiter));
    }

    @Test
    void chefCannotViewAnalytics() {
        assertThrows(SecurityException.class,
                () -> service.totalRevenueToday(chef));
    }

    // -------------------------------------------------------------
    // TOP SELLING ITEMS
    // -------------------------------------------------------------

    @Test
    void topSellingItems_aggregatesCorrectly() {
        // Order 1: PAID
        Order o1 = new Order(1, manager.id());
        o1.addItem(pasta);
        o1.addItem(pasta);
        o1.addItem(burger);
        o1.updateStatus(OrderStatus.SERVED);
        o1.processPayment(PaymentMethod.CREDIT_CARD);
        repo.save(o1);

        // Order 2: SERVED (still counts)
        Order o2 = new Order(2, manager.id());
        o2.addItem(burger);
        o2.addItem(drink);
        o2.updateStatus(OrderStatus.SERVED);
        repo.save(o2);

        Map<String, Long> result = service.topSellingItems(manager);

        assertEquals(2, result.get("Pasta"));   // sold twice
        assertEquals(2, result.get("Burger"));  // one in each order
        assertEquals(1, result.get("Cola"));    // one time
        assertEquals(3, result.size());
    }

    @Test
    void topSellingItems_emptyRepo_returnsEmptyMap() {
        assertTrue(service.topSellingItems(manager).isEmpty());
    }

    // -------------------------------------------------------------
    // REVENUE TODAY
    // -------------------------------------------------------------

    @Test
    void totalRevenueToday_sumsOnlyTodaysPaidOrders() {
        // Today PAID order
        Order today1 = new Order(1, manager.id());
        today1.addItem(pasta);
        today1.addItem(drink);
        today1.updateStatus(OrderStatus.SERVED);
        today1.processPayment(PaymentMethod.CREDIT_CARD); // makes it PAID
        repo.save(today1);

        // Today but SERVED only → does NOT count
        Order today2 = new Order(2, manager.id());
        today2.addItem(burger);
        today2.updateStatus(OrderStatus.SERVED);
        repo.save(today2);

        // Another PAID order with a different date → should NOT count
        Order old = new Order(3, manager.id());
        old.addItem(pasta);
        old.updateStatus(OrderStatus.SERVED);
        old.processPayment(PaymentMethod.CASH);
        forceCreatedAt(old, LocalDateTime.now().minusDays(1));
        repo.save(old);

        double revenue = service.totalRevenueToday(manager);
        assertEquals(12 + 3, revenue); // only order 1
    }

    @Test
    void totalRevenueToday_emptyRepo_returnsZero() {
        assertEquals(0.0, service.totalRevenueToday(manager));
    }

    private static void forceCreatedAt(Order order, LocalDateTime dt) {
        try {
            var field = Order.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(order, dt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
