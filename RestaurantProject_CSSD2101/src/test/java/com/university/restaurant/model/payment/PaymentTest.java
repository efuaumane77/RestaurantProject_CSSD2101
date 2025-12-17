//package com.university.restaurant.model.payment;

//public class PaymentTest {
//}

// ==================== PaymentTest.java ====================
package com.university.restaurant.model.payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testConstructorAndGetters() {
        Payment payment = new Payment(PaymentMethod.CASH, 25.50);

        assertEquals(25.50, payment.getAmount(), 0.001);
        assertNotNull(payment.getTransactionId());
        assertNotNull(payment.getTimestamp());
    }

    @Test
    void testTransactionIdFormat() {
        Payment payment = new Payment(PaymentMethod.CREDIT_CARD, 100.00);
        assertTrue(payment.getTransactionId().startsWith("TXN-"));
    }

    @Test
    void testToString() {
        Payment payment = new Payment(PaymentMethod.DEBIT_CARD, 50.75);
        String result = payment.toString();

        assertTrue(result.contains("DEBIT_CARD"));
        assertTrue(result.contains("50.75"));
        assertTrue(result.contains("TXN-"));
    }

    @Test
    void testAllPaymentMethods() {
        Payment cash = new Payment(PaymentMethod.CASH, 10.00);
        Payment credit = new Payment(PaymentMethod.CREDIT_CARD, 20.00);
        Payment debit = new Payment(PaymentMethod.DEBIT_CARD, 30.00);
        Payment mobile = new Payment(PaymentMethod.MOBILE, 40.00);

        assertEquals(10.00, cash.getAmount(), 0.001);
        assertEquals(20.00, credit.getAmount(), 0.001);
        assertEquals(30.00, debit.getAmount(), 0.001);
        assertEquals(40.00, mobile.getAmount(), 0.001);
    }
}