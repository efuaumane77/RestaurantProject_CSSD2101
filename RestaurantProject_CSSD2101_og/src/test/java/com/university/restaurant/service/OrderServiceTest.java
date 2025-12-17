package com.university.restaurant.service;

import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.*;
import com.university.restaurant.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    OrderService service;
    InMemoryOrderRepo repo;
    InMemoryRestaurantAuditRepo audits;

    Manager manager = new Manager("m1", "Alice");
    Waiter waiter = new Waiter("w1", "Bob");
    Chef chef = new Chef("c1", "Charles");

    MenuItem entree = new Entree(
            "i1",
            "Burger",
            "Beef burger",
            12.0,
            DietaryType.REGULAR,
            List.of("beef", "bun", "cheese"),
            10
    );

    MenuItem drink = new Drink("i2", "Coke", "Soda", 3.0, false);

    @BeforeEach
    void setup() {
        repo = new InMemoryOrderRepo();
        audits = new InMemoryRestaurantAuditRepo();
        service = new OrderService(repo, audits);
    }

    // ----------------------------------------------------
    // PLACE ORDER
    // ----------------------------------------------------

    @Test
    void managerCanPlaceOrder() {
        Order order = service.placeOrder(manager, "5", List.of(entree, drink));

        assertNotNull(order.getId());
        assertEquals(2, order.getItems().size());
        assertEquals(5, order.getTableNumber());
        assertEquals(1, audits.all().size());

        // verify repo saved it
        assertEquals(order, repo.findById(order.getId()).orElseThrow());
    }

    @Test
    void waiterCanPlaceOrder() {
        Order order = service.placeOrder(waiter, "3", List.of(drink));

        assertEquals(1, order.getItems().size());
        assertEquals(3, order.getTableNumber());
    }

    @Test
    void chefCannotPlaceOrder() {
        assertThrows(SecurityException.class,
                () -> service.placeOrder(chef, "2", List.of(entree)));
    }

    // ----------------------------------------------------
    // UPDATE ORDER STATUS
    // ----------------------------------------------------

    @Test
    void managerCanUpdateOrderStatus() {
        Order order = service.placeOrder(manager, "4", List.of(entree));
        String id = order.getId().toString();

        service.updateOrderStatus(manager, id, "served");

        Order updated = repo.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.SERVED, updated.getStatus());
        assertEquals(2, audits.all().size()); // one for place + one for update
    }

    @Test
    void waiterCanUpdateOrderStatus() {
        Order order = service.placeOrder(manager, "1", List.of(entree));
        String id = order.getId().toString();

        service.updateOrderStatus(waiter, id, "paid");

        assertEquals(OrderStatus.PAID, repo.findById(order.getId()).orElseThrow().getStatus());
    }

    @Test
    void chefCannotUpdateStatus() {
        Order order = service.placeOrder(manager, "7", List.of(entree));

        assertThrows(SecurityException.class,
                () -> service.updateOrderStatus(chef, order.getId().toString(), "served"));
    }

    @Test
    void updateOrderStatus_orderNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateOrderStatus(manager, UUID.randomUUID().toString(), "served"));
    }

    @Test
    void updateOrderStatus_invalidStatus_throwsEnumError() {
        Order order = service.placeOrder(manager, "6", List.of(drink));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateOrderStatus(manager, order.getId().toString(), "NOT_A_STATUS"));
    }

    // ----------------------------------------------------
    // GET ORDER
    // ----------------------------------------------------

    @Test
    void getOrder_returnsCorrectRecord() {
        Order order = service.placeOrder(manager, "1", List.of(drink));

        Order found = service.getOrder(order.getId().toString());
        assertEquals(order, found);
    }

    @Test
    void getOrder_notFoundThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getOrder(UUID.randomUUID().toString()));
    }
}
