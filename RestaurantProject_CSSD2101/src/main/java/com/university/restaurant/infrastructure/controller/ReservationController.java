package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.infrastructure.dto.DTOMapper;
import com.university.restaurant.infrastructure.dto.ReservationDTO;
import com.university.restaurant.infrastructure.entity.ReservationEntity;
import com.university.restaurant.infrastructure.jpa.ReservationJpaRepository;
import com.university.restaurant.model.reservation.ReservationStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.port.ReservationServicePort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Reservation operations.
 * Handles all reservation-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationServicePort reservationService;
    private final ReservationJpaRepository reservationRepo;
    private final DTOMapper dtoMapper;

    public ReservationController(ReservationServicePort reservationService,
                                 ReservationJpaRepository reservationRepo,
                                 DTOMapper dtoMapper) {
        this.reservationService = reservationService;
        this.reservationRepo = reservationRepo;
        this.dtoMapper = dtoMapper;
    }

    /**
     * GET /reservations - Get all reservations
     */
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationEntity> entities = reservationRepo.findAll();
        List<ReservationDTO> dtos = entities.stream()
                .map(dtoMapper::toReservationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /reservations/{id} - Get reservation by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable UUID id) {
        return reservationRepo.findById(id)
                .map(dtoMapper::toReservationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /reservations/active - Get active reservations
     */
    @GetMapping("/active")
    public ResponseEntity<List<ReservationDTO>> getActiveReservations() {
        List<ReservationEntity> entities = reservationRepo.findActiveReservations();
        List<ReservationDTO> dtos = entities.stream()
                .map(dtoMapper::toReservationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /reservations/date/{date} - Get reservations by date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ReservationEntity> entities = reservationRepo.findByDate(date);
        List<ReservationDTO> dtos = entities.stream()
                .map(dtoMapper::toReservationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /reservations/status/{status} - Get reservations by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByStatus(
            @PathVariable ReservationStatus status) {
        List<ReservationEntity> entities = reservationRepo.findByStatus(status);
        List<ReservationDTO> dtos = entities.stream()
                .map(dtoMapper::toReservationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /reservations - Create new reservation
     * Request Body: {
     *   "staffId": "w1",
     *   "staffName": "Bob",
     *   "staffRole": "WAITER",
     *   "customerName": "John Doe",
     *   "customerPhone": "555-1234",
     *   "customerEmail": "john@example.com",
     *   "partySize": 4,
     *   "reservationTime": "2024-12-20T19:00:00"
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            String roleStr = (String) request.get("staffRole");

            StaffRole staff = createStaffRole(staffId, staffName, roleStr);

            String customerName = (String) request.get("customerName");
            String customerPhone = (String) request.get("customerPhone");
            String customerEmail = (String) request.get("customerEmail");
            Integer partySize = ((Number) request.get("partySize")).intValue();
            LocalDateTime reservationTime = LocalDateTime.parse((String) request.get("reservationTime"));

            reservationService.createReservation(staff, customerName, customerPhone,
                    customerEmail, partySize, reservationTime);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Reservation created successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /reservations/{id} - Cancel reservation
     * Request Body: { "staffId": "w1", "staffName": "Bob", "staffRole": "WAITER" }
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelReservation(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            String roleStr = (String) request.get("staffRole");

            StaffRole staff = createStaffRole(staffId, staffName, roleStr);

            boolean cancelled = reservationService.cancelReservation(staff, id.toString());

            if (cancelled) {
                return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
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