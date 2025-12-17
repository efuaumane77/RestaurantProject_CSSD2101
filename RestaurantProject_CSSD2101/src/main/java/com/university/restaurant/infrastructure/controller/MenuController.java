package com.university.restaurant.infrastructure.controller;

import com.university.restaurant.infrastructure.dto.DTOMapper;
import com.university.restaurant.infrastructure.dto.MenuItemDTO;
import com.university.restaurant.infrastructure.entity.MenuItemEntity;
import com.university.restaurant.infrastructure.jpa.MenuJpaRepository;
import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.port.MenuServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Menu operations.
 * Handles all menu-related HTTP requests.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuServicePort menuService;
    private final MenuJpaRepository menuRepo;
    private final DTOMapper dtoMapper;

    public MenuController(MenuServicePort menuService,
                          MenuJpaRepository menuRepo,
                          DTOMapper dtoMapper) {
        this.menuService = menuService;
        this.menuRepo = menuRepo;
        this.dtoMapper = dtoMapper;
    }

    /**
     * GET /menu - Get all menu items
     */
    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        List<MenuItemEntity> entities = menuRepo.findAll();
        List<MenuItemDTO> dtos = entities.stream()
                .map(dtoMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /menu/available - Get only available menu items
     */
    @GetMapping("/available")
    public ResponseEntity<List<MenuItemDTO>> getAvailableMenuItems() {
        List<MenuItemEntity> entities = menuRepo.findByAvailableTrue();
        List<MenuItemDTO> dtos = entities.stream()
                .map(dtoMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /menu/{id} - Get menu item by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable String id) {
        return menuRepo.findById(id)
                .map(dtoMapper::toMenuItemDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /menu/category/{category} - Get items by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(
            @PathVariable MenuCategory category) {
        List<MenuItemEntity> entities = menuRepo.findByCategory(category);
        List<MenuItemDTO> dtos = entities.stream()
                .map(dtoMapper::toMenuItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /menu - Add new menu item
     * Request Body: { "staffId": "m1", "staffName": "Alice", "item": {...} }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addMenuItem(@RequestBody Map<String, Object> request) {
        try {
            // Extract staff info (simplified - assumes Manager)
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            Manager manager = new Manager(staffId, staffName);

            // Extract item data
            @SuppressWarnings("unchecked")
            Map<String, Object> itemData = (Map<String, Object>) request.get("item");

            MenuItem item = buildMenuItemFromRequest(itemData);

            // Call service
            menuService.addMenuItem(manager, item);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Menu item added successfully", "id", item.getId()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /menu/{id}/price - Update menu item price
     * Request Body: { "staffId": "m1", "staffName": "Alice", "newPrice": 15.99 }
     */
    @PutMapping("/{id}/price")
    public ResponseEntity<Map<String, String>> updatePrice(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        try {
            String staffId = (String) request.get("staffId");
            String staffName = (String) request.get("staffName");
            Manager manager = new Manager(staffId, staffName);

            Double newPrice = ((Number) request.get("newPrice")).doubleValue();

            menuService.updatePrice(manager, id, newPrice);

            return ResponseEntity.ok(Map.of("message", "Price updated successfully"));
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
     * Helper method to build MenuItem from request data
     */
    private MenuItem buildMenuItemFromRequest(Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        String description = (String) data.get("description");
        Double price = ((Number) data.get("price")).doubleValue();
        String type = (String) data.get("type");

        return switch (type.toUpperCase()) {
            case "ENTREE" -> {
                DietaryType dietaryType = DietaryType.valueOf((String) data.get("dietaryType"));
                @SuppressWarnings("unchecked")
                List<String> ingredients = (List<String>) data.get("ingredients");
                Integer prepTime = ((Number) data.get("prepTimeMinutes")).intValue();
                yield new Entree(id, name, description, price, dietaryType, ingredients, prepTime);
            }
            case "DRINK" -> {
                Boolean isAlcoholic = (Boolean) data.get("isAlcoholic");
                yield new Drink(id, name, description, price, isAlcoholic);
            }
            case "DESSERT" -> {
                DietaryType dietaryType = DietaryType.valueOf((String) data.get("dietaryType"));
                @SuppressWarnings("unchecked")
                List<String> allergens = (List<String>) data.get("allergens");
                yield new Dessert(id, name, description, price, dietaryType, allergens);
            }
            default -> throw new IllegalArgumentException("Unknown menu item type: " + type);
        };
    }
}