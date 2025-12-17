package com.university.restaurant.service;

import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.order.*;
import com.university.restaurant.model.payment.*;
import com.university.restaurant.model.staff.*;
import com.university.restaurant.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    InMemoryOrderRepo orderRepo;
    InMemoryPaymentRepo paymentRepo;
    InMemoryRestaurantAuditRepo audits;
    PaymentService service;

    Manager manager = new Manager("m1", "Alice Manager");
    Waiter waiter  = new Waiter("w1", "Bob Waiter");
    Chef chef      = new Chef("c1", "Charlie Chef");

    MenuItem pasta;

    @BeforeEach
    void setup() {
        orderRepo = new InMemoryOrderRepo();
        paymentRepo = new InMemoryPaymentRepo();
        audits = new InMemoryRestaurantAuditRepo();

        service = new PaymentService(orderRepo, paymentRepo, audits);

        pasta = new Entree("i1", "Pasta", "Classic pasta", 12.50,
                DietaryType.REGULAR, List.of("flour", "sauce"), 10);
    }

    // ---------------------------------------------------------
    // PERMISSION TESTS
    // ---------------------------------------------------------

    @Test
    void waiterCanCompletePayment() {
        // Arrange
        Order order = new Order(1, waiter.id());
        order.addItem(pasta);
        order.updateStatus(OrderStatus.SERVED);
        orderRepo.save(order);

        // Act
        Payment p = service.completePayment(waiter, order.getId().toString(), PaymentMethod.CREDIT_CARD);

        // Assert
        assertNotNull(p);
        assertEquals(order.getPayment().getTransactionId(), p.getTransactionId());
        assertEquals(12.50, p.getAmount(), 0.01);

        // Order updated correctly
        Order updated = orderRepo.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, updated.getStatus());

        // Payment stored
        assertEquals(1, paymentRepo.findAll().size());

        // Audit log recorded
        assertEquals(1, audits.all().size());
    }

    @Test
    void chefCannotViewPayment() {
        Order order = new Order(1, manager.id());
        order.addItem(pasta);
        order.updateStatus(OrderStatus.SERVED);
        order.processPayment(PaymentMethod.CREDIT_CARD);
        orderRepo.save(order);

        assertThrows(SecurityException.class,
                () -> service.getPaymentForOrder(chef, order.getId().toString()));
    }

    // ---------------------------------------------------------
    // COMPLETE PAYMENT
    // ---------------------------------------------------------

    @Test
    void managerCanCompletePayment() {
        Order order = new Order(1, manager.id());
        order.addItem(pasta);
        order.updateStatus(OrderStatus.SERVED);
        orderRepo.save(order);

        Payment result = service.completePayment(manager, order.getId().toString(), PaymentMethod.CREDIT_CARD);

        assertNotNull(result);
        assertEquals(12.50, result.getAmount(), 0.01);

        // order updated
        Order updated = orderRepo.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, updated.getStatus());
        assertNotNull(updated.getPayment());

        // payment stored in repo
        assertEquals(1, paymentRepo.findAll().size());

        // audit log recorded
        assertEquals(1, audits.all().size());
    }

    @Test
    void completePaymentFailsIfOrderNotServed() {
        Order order = new Order(1, manager.id()); // PENDING
        order.addItem(pasta);
        orderRepo.save(order);

        assertThrows(IllegalStateException.class,
                () -> service.completePayment(manager, order.getId().toString(), PaymentMethod.CASH));
    }

    @Test
    void completePayment_orderNotFoundThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.completePayment(manager, UUID.randomUUID().toString(), PaymentMethod.CREDIT_CARD));
    }

    // ---------------------------------------------------------
    // GET PAYMENT FOR ORDER
    // ---------------------------------------------------------

    @Test
    void managerCanGetPaymentForOrder() {
        // Arrange
        Order order = new Order(1, manager.id());
        order.addItem(pasta);
        order.updateStatus(OrderStatus.SERVED);
        order.processPayment(PaymentMethod.DEBIT_CARD);
        orderRepo.save(order);

        // Act
        Payment p = service.getPaymentForOrder(manager, order.getId().toString());

        // Assert
        assertEquals(order.getPayment().getTransactionId(), p.getTransactionId());
        assertEquals(1, audits.all().size());
    }

    @Test
    void getPaymentForOrder_throwsIfOrderNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getPaymentForOrder(manager, UUID.randomUUID().toString()));
    }

    @Test
    void getPaymentForOrder_throwsIfNotPaidYet() {
        Order order = new Order(1, manager.id());
        order.addItem(pasta);
        order.updateStatus(OrderStatus.SERVED);
        orderRepo.save(order); // but no payment yet

        assertThrows(IllegalStateException.class,
                () -> service.getPaymentForOrder(manager, order.getId().toString()));
    }

}
