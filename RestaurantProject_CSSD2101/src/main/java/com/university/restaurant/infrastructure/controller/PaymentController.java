package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.infrastructure.dto.DTOMapper;
import com.university.restaurant.infrastructure.dto.PaymentDTO;
import com.university.restaurant.infrastructure.entity.PaymentEntity;
import com.university.restaurant.infrastructure.jpa.PaymentJpaRepository;
import com.university.restaurant.model.payment.Payment;
import com.university.restaurant.model.payment.PaymentMethod;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.port.PaymentServicePort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Payment operations.
 * Handles all payment-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentServicePort paymentService;
    private final PaymentJpaRepository paymentRepo;
    private final DTOMapper dtoMapper;

    public PaymentController(PaymentServicePort paymentService,
                             PaymentJpaRepository paymentRepo,
                             DTOMapper dtoMapper) {
        this.paymentService = paymentService;
        this.paymentRepo = paymentRepo;
        this.dtoMapper = dtoMapper;
    }

    /**
     * GET /payments - Get all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentEntity> entities = paymentRepo.findAll();
        List<PaymentDTO> dtos = entities.stream()
                .map(dtoMapper::toPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /payments/{transactionId} - Get payment by transaction ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentRepo.findById(transactionId)
                .map(dtoMapper::toPaymentDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /payments/order/{orderId} - Get payment for an order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentForOrder(@PathVariable UUID orderId) {
        return paymentRepo.findByOrderId(orderId)
                .map(dtoMapper::toPaymentDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /payments/date/{date} - Get payments by date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PaymentEntity> entities = paymentRepo.findByDate(date);
        List<PaymentDTO> dtos = entities.stream()
                .map(dtoMapper::toPaymentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /payments/complete - Complete payment for an order
     * Request Body: {
     *   "staffId": "w1",
     *   "staffName": "Bob",
     *   "staffRole": "WAITER",
     *   "orderId": "uuid-here",
     *   "paymentMethod": "CREDIT_CARD"
     * }
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completePayment(@RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            String roleStr = (String) request.get("staffRole");

            StaffRole staff = createStaffRole(staffId, staffName, roleStr);

            String orderId = (String) request.get("orderId");
            PaymentMethod method = PaymentMethod.valueOf((String) request.get("paymentMethod"));

            Payment payment = paymentService.completePayment(staff, orderId, method);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Payment completed successfully",
                            "transactionId", payment.getTransactionId(),
                            "amount", payment.getAmount()
                    ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /payments/revenue/total - Get total revenue
     */
    @GetMapping("/revenue/total")
    public ResponseEntity<Map<String, Double>> getTotalRevenue() {
        Double totalRevenue = paymentRepo.findAll().stream()
                .mapToDouble(PaymentEntity::getAmount)
                .sum();
        return ResponseEntity.ok(Map.of("totalRevenue", totalRevenue));
    }

    /**
     * GET /payments/revenue/date/{date} - Get revenue for a specific date
     */
    @GetMapping("/revenue/date/{date}")
    public ResponseEntity<Map<String, Double>> getRevenueByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double revenue = paymentRepo.getTotalAmountForDate(date);
        return ResponseEntity.ok(Map.of("revenue", revenue != null ? revenue : 0.0));
    }

    /**
     * Helper method to create StaffRole
     */
    private StaffRole createStaffRole(String id, String name, String role) {
        return switch (role.toUpperCase()) {
            case "MANAGER" -> new Manager(id, name);
            case "WAITER" -> new Waiter(id, name);
            default -> throw new IllegalArgumentException("Invalid staff role: " + role);
        };
    }
}
