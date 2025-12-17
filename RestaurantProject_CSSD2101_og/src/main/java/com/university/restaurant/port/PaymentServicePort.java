package com.university.restaurant.port;

import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Defines the contract for payment-related use cases in the restaurant system.
 * This port is part of the application layer in a Hexagonal Architecture and
 * provides an abstraction for processing payments and retrieving payment records.
 * </p>
 *
 * <p>
 * Implementations must enforce role-based access control (e.g., manager and waiter),
 * apply domain rules (such as requiring the order to be SERVED before payment),
 * and interact with persistence layers via payment and order repositories.
 * </p>
 */
public interface PaymentServicePort {

    /**
     * <p>
     * Completes the payment for an order and transitions the order to a PAID state.
     * A new {@link Payment} entity is created using the provided method
     * (e.g., CASH, CARD) and stored through the payment repository.
     * </p>
     *
     * <p>
     * Implementations must:
     * <ul>
     *     <li>Verify that the actor has sufficient permissions</li>
     *     <li>Validate that the order exists</li>
     *     <li>Ensure the order is in SERVED state before payment</li>
     *     <li>Store the resulting payment record</li>
     *     <li>Append an audit log entry</li>
     * </ul>
     * </p>
     *
     * @param actor   the staff role attempting to complete the payment
     * @param orderId the identifier of the order being paid (UUID string)
     * @param method  the payment method used (CARD, CASH, etc.)
     * @return the {@link Payment} instance representing the transaction
     *
     * @throws SecurityException         if the actor is not authorized
     * @throws IllegalArgumentException  if the order does not exist
     * @throws IllegalStateException     if the order is not ready for payment
     */
    Payment completePayment(StaffRole actor, String orderId, PaymentMethod method);

    /**
     * <p>
     * Retrieves the {@link Payment} associated with a specific order.
     * Used primarily by authorized staff roles such as managers or waiters.
     * </p>
     *
     * <p>
     * Implementations must:
     * <ul>
     *     <li>Validate permissions</li>
     *     <li>Ensure the order exists</li>
     *     <li>Verify a payment actually exists</li>
     *     <li>Emit an audit trail event</li>
     * </ul>
     * </p>
     *
     * @param actor   the staff role requesting to view the payment
     * @param orderId the order's UUID string
     * @return the {@link Payment} record linked to the order
     *
     * @throws SecurityException         if the role lacks permission
     * @throws IllegalArgumentException  if no such order exists
     * @throws IllegalStateException     if the order has not yet been paid
     */
    Payment getPaymentForOrder(StaffRole actor, String orderId);
}
