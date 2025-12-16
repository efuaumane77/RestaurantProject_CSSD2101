package com.university.restaurant.service;

import com.university.restaurant.chain.payment.PaymentPermissionChain;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.PaymentServicePort;
import com.university.restaurant.repository.OrderRepository;
import com.university.restaurant.repository.PaymentRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.util.UUID;

/**
 * <p>
 * Service-layer implementation of {@link PaymentServicePort}, responsible for
 * completing payments and retrieving payment records within the restaurant
 * management system.
 * </p>
 *
 * <p>
 * This class enforces domain rules (e.g., only SERVED orders may be paid),
 * ensures role-based permissions via {@link PaymentPermissionChain},
 * persists changes through the provided repositories, and records all
 * operations in the audit log.
 * </p>
 *
 * <p>
 * The service adheres to Hexagonal Architecture principles by depending only
 * on the {@link PaymentServicePort} abstraction and repository interfaces,
 * not on infrastructure details.
 * </p>
 */
public class PaymentService implements PaymentServicePort {

    private final OrderRepository orders;
    private final PaymentRepository payments;
    private final RestaurantAuditLogRepository audits;
    private final PaymentPermissionChain permissions = new PaymentPermissionChain();

    /**
     * Constructs the {@code PaymentService} with the required repository
     * dependencies and audit log.
     *
     * @param orders   repository for retrieving and saving orders
     * @param payments repository for persisting payment records
     * @param audits   repository for writing audit trail entries
     */
    public PaymentService(OrderRepository orders,
                          PaymentRepository payments,
                          RestaurantAuditLogRepository audits) {
        this.orders = orders;
        this.payments = payments;
        this.audits = audits;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * </p>
     * <ul>
     *     <li>Validates role-based permissions</li>
     *     <li>Loads the target order</li>
     *     <li>Ensures the order is eligible for payment (must be SERVED)</li>
     *     <li>Applies domain logic to mark the order as PAID</li>
     *     <li>Saves the updated order</li>
     *     <li>Persists the generated {@link Payment}</li>
     *     <li>Writes an audit entry</li>
     * </ul>
     */
    @Override
    public Payment completePayment(StaffRole actor, String orderId, PaymentMethod method) {

        // Permission check
        permissions.check(actor, "complete a payment");

        UUID id = UUID.fromString(orderId);

        // 1. Load order
        Order order = orders.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // 2. Domain rule: payment allowed only after SERVED
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException("Order must be SERVED before payment.");
        }

        // 3. Process payment using domain logic
        order.processPayment(method);

        // 4. Save updated order
        orders.save(order);

        // 5. Store the payment record
        Payment p = order.getPayment();
        payments.save(p);

        // 6. Audit log
        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "COMPLETE_PAYMENT",
                "Order",
                orderId,
                "Completed payment using " + method + " for amount $" + p.getAmount(),
                audits.tailHash()
        ));

        return p;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * </p>
     * <ul>
     *     <li>Checks permission to view payment details</li>
     *     <li>Retrieves the order and verifies its existence</li>
     *     <li>Ensures the order has an associated {@link Payment}</li>
     *     <li>Records an audit entry</li>
     *     <li>Returns the payment object</li>
     * </ul>
     */
    @Override
    public Payment getPaymentForOrder(StaffRole actor, String orderId) {

        // Permission check
        permissions.check(actor, "view a payment");

        UUID id = UUID.fromString(orderId);

        // 1. Load the order
        Order order = orders.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found: " + orderId)
                );

        // 2. Ensure order actually HAS a payment
        Payment payment = order.getPayment();
        if (payment == null) {
            throw new IllegalStateException("Order " + orderId + " has not been paid yet.");
        }

        // 3. Audit
        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "GET_PAYMENT_FOR_ORDER",
                "Payment",
                payment.getTransactionId(),
                "Retrieved payment for order " + orderId,
                audits.tailHash()
        ));

        // 4. Return the payment
        return payment;
    }

}
