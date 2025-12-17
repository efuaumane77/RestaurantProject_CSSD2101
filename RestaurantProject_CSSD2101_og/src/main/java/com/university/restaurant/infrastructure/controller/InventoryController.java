package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.infrastructure.dto.DTOMapper;
import com.university.restaurant.infrastructure.dto.InventoryDTO;
import com.university.restaurant.infrastructure.entity.InventoryEntity;
import com.university.restaurant.infrastructure.jpa.InventoryJpaRepository;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.port.InventoryServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Inventory operations.
 * Handles all inventory-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryServicePort inventoryService;
    private final InventoryJpaRepository inventoryRepo;
    private final DTOMapper dtoMapper;

    public InventoryController(InventoryServicePort inventoryService,
                               InventoryJpaRepository inventoryRepo,
                               DTOMapper dtoMapper) {
        this.inventoryService = inventoryService;
        this.inventoryRepo = inventoryRepo;
        this.dtoMapper = dtoMapper;
    }

    /**
     * GET /inventory - Get all inventory items
     */
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventoryItems() {
        List<InventoryEntity> entities = inventoryRepo.findAll();
        List<InventoryDTO> dtos = entities.stream()
                .map(dtoMapper::toInventoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /inventory/{id} - Get inventory item by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryItemById(@PathVariable String id) {
        return inventoryRepo.findById(id)
                .map(dtoMapper::toInventoryDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /inventory/low-stock - Get items with low stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems() {
        List<InventoryEntity> entities = inventoryRepo.findLowStockItems();
        List<InventoryDTO> dtos = entities.stream()
                .map(dtoMapper::toInventoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /inventory/out-of-stock - Get items that are out of stock
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryDTO>> getOutOfStockItems() {
        List<InventoryEntity> entities = inventoryRepo.findOutOfStockItems();
        List<InventoryDTO> dtos = entities.stream()
                .map(dtoMapper::toInventoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /inventory/{id}/reduce - Reduce stock
     * Request Body: { "staffId": "m1", "staffName": "Alice", "quantity": 5 }
     */
    @PostMapping("/{id}/reduce")
    public ResponseEntity<Map<String, String>> reduceStock(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            Manager manager = new Manager(staffId, staffName);

            Integer quantity = ((Number) request.get("quantity")).intValue();

            inventoryService.reduceStock(manager, id, quantity);

            return ResponseEntity.ok(Map.of("message", "Stock reduced successfully"));
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
     * POST /inventory/{id}/increase - Increase stock (restock)
     * Request Body: { "staffId": "m1", "staffName": "Alice", "quantity": 20 }
     */
    @PostMapping("/{id}/increase")
    public ResponseEntity<Map<String, String>> increaseStock(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            Manager manager = new Manager(staffId, staffName);

            Integer quantity = ((Number) request.get("quantity")).intValue();

            inventoryService.increaseStock(manager, id, quantity);

            return ResponseEntity.ok(Map.of("message", "Stock increased successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /inventory/{id}/stock-level - Get current stock level
     */
    @GetMapping("/{id}/stock-level")
    public ResponseEntity<Map<String, Integer>> getStockLevel(@PathVariable String id) {
        try {
            int stockLevel = inventoryService.getStockLevel(id);
            return ResponseEntity.ok(Map.of("stockLevel", stockLevel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
