package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.infrastructure.dto.DTOMapper;
import com.university.restaurant.infrastructure.dto.OrderDTO;
import com.university.restaurant.infrastructure.entity.OrderEntity;
import com.university.restaurant.infrastructure.jpa.MenuJpaRepository;
import com.university.restaurant.infrastructure.jpa.OrderJpaRepository;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.port.OrderServicePort;
import com.university.restaurant.infrastructure.adapter.mapper.MenuItemMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Order operations.
 * Handles all order-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServicePort orderService;
    private final OrderJpaRepository orderRepo;
    private final MenuJpaRepository menuRepo;
    private final MenuItemMapper menuMapper;
    private final DTOMapper dtoMapper;

    public OrderController(OrderServicePort orderService,
                           OrderJpaRepository orderRepo,
                           MenuJpaRepository menuRepo,
                           MenuItemMapper menuMapper,
                           DTOMapper dtoMapper) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.menuRepo = menuRepo;
        this.menuMapper = menuMapper;
        this.dtoMapper = dtoMapper;
    }

    /**
     * GET /orders - Get all orders
     */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderEntity> entities = orderRepo.findAll();
        List<OrderDTO> dtos = entities.stream()
                .map(dtoMapper::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /orders/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID id) {
        return orderRepo.findById(id)
                .map(dtoMapper::toOrderDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /orders/status/{status} - Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderEntity> entities = orderRepo.findByStatus(status);
        List<OrderDTO> dtos = entities.stream()
                .map(dtoMapper::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /orders/table/{tableNumber} - Get orders by table
     */
    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<OrderDTO>> getOrdersByTable(@PathVariable Integer tableNumber) {
        List<OrderEntity> entities = orderRepo.findByTableNumber(tableNumber);
        List<OrderDTO> dtos = entities.stream()
                .map(dtoMapper::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /orders - Place new order
     * Request Body: {
     *   "staffId": "w1",
     *   "staffName": "Bob",
     *   "staffRole": "WAITER",
     *   "tableId": "5",
     *   "itemIds": ["item1", "item2"]
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody Map<String, Object> request) {
        try {
            // Extract staff info
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            String roleStr = (String) request.get("staffRole");

            StaffRole staff = createStaffRole(staffId, staffName, roleStr);

            String tableId = (String) request.get("tableId");

            @SuppressWarnings("unchecked")
            List<String> itemIds = (List<String>) request.get("itemIds");

            // Fetch menu items
            List<MenuItem> items = itemIds.stream()
                    .map(id -> menuRepo.findById(id)
                            .map(menuMapper::toDomain)
                            .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id)))
                    .collect(Collectors.toList());

            // Place order through service
            orderService.placeOrder(staff, tableId, items);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Order placed successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /orders/{id}/status - Update order status
     * Request Body: {
     *   "staffId": "w1",
     *   "staffName": "Bob",
     *   "staffRole": "WAITER",
     *   "newStatus": "SERVED"
     * }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            String roleStr = (String) request.get("staffRole");

            StaffRole staff = createStaffRole(staffId, staffName, roleStr);

            String newStatus = (String) request.get("newStatus");

            orderService.updateOrderStatus(staff, id.toString(), newStatus);

            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Helper method to create StaffRole based on role string
     */
    private StaffRole createStaffRole(String id, String name, String role) {
        return switch (role.toUpperCase()) {
            case "MANAGER" -> new Manager(id, name);
            case "WAITER" -> new Waiter(id, name);
            default -> throw new IllegalArgumentException("Invalid staff role: " + role);
        };
    }
}
