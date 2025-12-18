package com.university.restaurant.service;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.Chef;
import com.university.restaurant.repository.OrderRepository;
import com.university.restaurant.repository.PaymentRepository;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private PaymentRepository paymentRepo;

    @Mock
    private RestaurantAuditLogRepository auditRepo;

    private PaymentService service;
    private Manager manager;
    private Waiter waiter;
    private Chef chef;

    @BeforeEach
    void setUp() {
        service = new PaymentService(orderRepo, paymentRepo, auditRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
        chef = new Chef("c1", "Charlie");
        when(auditRepo.tailHash()).thenReturn("GENESIS");
    }

    @Test
    void completePayment_withWaiterRole_shouldSucceed() {
        Order order = new Order(1, "w1");
        order.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order.updateStatus(OrderStatus.SERVED);
        UUID orderId = order.getId();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        Payment payment = service.completePayment(waiter, orderId.toString(), PaymentMethod.CASH);

        assertNotNull(payment);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepo).save(order);
        verify(paymentRepo).save(payment);
        verify(auditRepo).append(any());
    }

    @Test
    void completePayment_withManagerRole_shouldSucceed() {
        Order order = new Order(1, "w1");
        order.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order.updateStatus(OrderStatus.SERVED);
        UUID orderId = order.getId();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        Payment payment = service.completePayment(manager, orderId.toString(), PaymentMethod.CREDIT_CARD);

        assertNotNull(payment);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void completePayment_withChefRole_shouldThrowSecurityException() {
        assertThrows(SecurityException.class, () -> {
            service.completePayment(chef, UUID.randomUUID().toString(), PaymentMethod.CASH);
        });
        verify(paymentRepo, never()).save(any());
    }

    @Test
    void completePayment_orderNotServed_shouldThrowException() {
        Order order = new Order(1, "w1");
        order.updateStatus(OrderStatus.PENDING);
        UUID orderId = order.getId();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> {
            service.completePayment(waiter, orderId.toString(), PaymentMethod.CASH);
        });
    }

    @Test
    void completePayment_nonExistentOrder_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(orderRepo.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.completePayment(waiter, randomId.toString(), PaymentMethod.CASH);
        });
    }

    @Test
    void getPaymentForOrder_existingPayment_shouldReturnPayment() {
        Order order = new Order(1, "w1");
        order.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order.updateStatus(OrderStatus.SERVED);
        order.processPayment(PaymentMethod.CASH);
        UUID orderId = order.getId();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        Payment payment = service.getPaymentForOrder(waiter, orderId.toString());

        assertNotNull(payment);
        verify(auditRepo).append(any());
    }

    @Test
    void getPaymentForOrder_noPayment_shouldThrowException() {
        Order order = new Order(1, "w1");
        UUID orderId = order.getId();

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> {
            service.getPaymentForOrder(waiter, orderId.toString());
        });
    }
}
