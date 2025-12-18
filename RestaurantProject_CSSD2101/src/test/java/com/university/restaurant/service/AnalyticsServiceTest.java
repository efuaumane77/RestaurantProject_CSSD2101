package com.university.restaurant.service;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepo;

    private AnalyticsService service;
    private Manager manager;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService(orderRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
    }

    @Test
    void topSellingItems_withManagerRole_shouldReturnStats() {
        Order order1 = new Order(1, "w1");
        order1.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order1.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order1.updateStatus(OrderStatus.PAID);

        Order order2 = new Order(2, "w1");
        order2.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order2.addItem(new Drink("d2", "Sprite", "desc", 2.99, false));
        order2.updateStatus(OrderStatus.SERVED);

        when(orderRepo.findByStatus(OrderStatus.PAID)).thenReturn(List.of(order1));
        when(orderRepo.findByStatus(OrderStatus.SERVED)).thenReturn(List.of(order2));

        Map<String, Long> topSelling = service.topSellingItems(manager);

        assertEquals(2, topSelling.size());
        assertEquals(3L, topSelling.get("Coke"));
        assertEquals(1L, topSelling.get("Sprite"));
    }

    @Test
    void topSellingItems_withWaiterRole_shouldThrowSecurityException() {
        assertThrows(SecurityException.class, () -> {
            service.topSellingItems(waiter);
        });
    }

    @Test
    void totalRevenueToday_withManagerRole_shouldReturnTotal() {
        Order order1 = new Order(1, "w1");
        order1.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order1.updateStatus(OrderStatus.PAID);

        Order order2 = new Order(2, "w1");
        order2.addItem(new Drink("d2", "Sprite", "desc", 3.99, false));
        order2.updateStatus(OrderStatus.PAID);

        when(orderRepo.findByStatus(OrderStatus.PAID)).thenReturn(List.of(order1, order2));

        double revenue = service.totalRevenueToday(manager);

        assertEquals(6.98, revenue, 0.01);
    }

    @Test
    void totalRevenueToday_noOrders_shouldReturnZero() {
        when(orderRepo.findByStatus(OrderStatus.PAID)).thenReturn(List.of());

        double revenue = service.totalRevenueToday(manager);

        assertEquals(0.0, revenue, 0.01);
    }

    @Test
    void totalRevenueToday_withWaiterRole_shouldThrowSecurityException() {
        assertThrows(SecurityException.class, () -> {
            service.totalRevenueToday(waiter);
        });
    }
}
