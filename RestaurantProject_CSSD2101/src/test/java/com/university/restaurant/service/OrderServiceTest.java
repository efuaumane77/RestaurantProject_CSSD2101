package com.university.restaurant.service;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.Chef;
import com.university.restaurant.repository.OrderRepository;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private RestaurantAuditLogRepository auditRepo;

    private OrderService service;
    private Manager manager;
    private Waiter waiter;
    private Chef chef;

    @BeforeEach
    void setUp() {
        service = new OrderService(orderRepo, auditRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
        chef = new Chef("c1", "Charlie");
        when(auditRepo.tailHash()).thenReturn("GENESIS");
    }

    @Test
    void placeOrder_withWaiterRole_shouldSucceed() {
        List<MenuItem> items = List.of(
            new Drink("d1", "Coke", "Soft drink", 2.99, false),
            new Drink("d2", "Sprite", "Soft drink", 2.99, false)
        );

        Order order = service.placeOrder(waiter, "5", items);

        assertNotNull(order);
        assertEquals(5, order.getTableNumber());
        assertEquals(2, order.getItems().size());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(orderRepo).save(order);
        verify(auditRepo).append(any());
    }

    @Test
    void placeOrder_withManagerRole_shouldSucceed() {
        List<MenuItem> items = List.of(new Drink("d1", "Coke", "desc", 2.99, false));

        Order order = service.placeOrder(manager, "3", items);

        assertNotNull(order);
        assertEquals(3, order.getTableNumber());
        verify(orderRepo).save(order);
    }

    @Test
    void placeOrder_withChefRole_shouldThrowSecurityException() {
        List<MenuItem> items = List.of(new Drink("d1", "Coke", "desc", 2.99, false));

        assertThrows(SecurityException.class, () -> {
            service.placeOrder(chef, "1", items);
        });
        verify(orderRepo, never()).save(any());
    }

    @Test
    void placeOrder_withEmptyItems_shouldCreateEmptyOrder() {
        Order order = service.placeOrder(waiter, "1", List.of());

        assertNotNull(order);
        assertTrue(order.getItems().isEmpty());
        assertEquals(0.0, order.calculateTotal());
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        Order order = new Order(5, "w1");
        UUID orderId = order.getId();
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        service.updateOrderStatus(waiter, orderId.toString(), "SERVED");

        assertEquals(OrderStatus.SERVED, order.getStatus());
        verify(orderRepo).save(order);
        verify(auditRepo).append(any());
    }

    @Test
    void updateOrderStatus_nonExistentOrder_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(orderRepo.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(waiter, randomId.toString(), "SERVED");
        });
    }

    @Test
    void updateOrderStatus_invalidStatus_shouldThrowException() {
        Order order = new Order(5, "w1");
        UUID orderId = order.getId();
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(waiter, orderId.toString(), "INVALID_STATUS");
        });
    }

    @Test
    void getOrder_existingOrder_shouldReturnOrder() {
        Order order = new Order(3, "w1");
        UUID orderId = order.getId();
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        Order retrieved = service.getOrder(orderId.toString());

        assertNotNull(retrieved);
        assertEquals(orderId, retrieved.getId());
    }

    @Test
    void getOrder_nonExistentOrder_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(orderRepo.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.getOrder(randomId.toString());
        });
    }
}
