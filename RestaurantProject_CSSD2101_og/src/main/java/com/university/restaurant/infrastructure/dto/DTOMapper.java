package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.infrastructure.entity.*;
import org.springframework.stereotype.Component;
import com.university.restaurant.infrastructure.entity.EntreeEntity;
import com.university.restaurant.infrastructure.entity.DrinkEntity;
import com.university.restaurant.infrastructure.entity.DessertEntity;
import com.university.restaurant.infrastructure.entity.ComboEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting JPA entities to DTOs.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class DTOMapper {

    /**
     * Convert MenuItemEntity to MenuItemDTO (polymorphic).
     */
    public MenuItemDTO toMenuItemDTO(MenuItemEntity entity) {
        if (entity instanceof EntreeEntity entree) {
            List<String> ingredients = entree.getIngredients() != null
                    ? Arrays.asList(entree.getIngredients().split(","))
                    : List.of();

            return new EntreeDTO(
                    entree.getId(),
                    entree.getName(),
                    entree.getDescription(),
                    entree.getPrice(),
                    entree.getDietaryType(),
                    entree.getAvailable(),
                    ingredients,
                    entree.getPrepTimeMinutes()
            );

        } else if (entity instanceof DrinkEntity drink) {
            return new DrinkDTO(
                    drink.getId(),
                    drink.getName(),
                    drink.getDescription(),
                    drink.getPrice(),
                    drink.getAvailable(),
                    drink.getIsAlcoholic()
            );

        } else if (entity instanceof DessertEntity dessert) {
            List<String> allergens = dessert.getAllergens() != null
                    ? Arrays.asList(dessert.getAllergens().split(","))
                    : List.of();

            return new DessertDTO(
                    dessert.getId(),
                    dessert.getName(),
                    dessert.getDescription(),
                    dessert.getPrice(),
                    dessert.getDietaryType(),
                    dessert.getAvailable(),
                    allergens
            );

        } else if (entity instanceof ComboEntity combo) {
            List<String> itemIds = combo.getItemIds() != null
                    ? Arrays.asList(combo.getItemIds().split(","))
                    : List.of();

            return new ComboDTO(
                    combo.getId(),
                    combo.getName(),
                    combo.getDescription(),
                    combo.getPrice(),
                    combo.getAvailable(),
                    itemIds,
                    combo.getDiscountPercent()
            );
        }

        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    /**
     * Convert OrderEntity to OrderDTO.
     */
    public OrderDTO toOrderDTO(OrderEntity entity) {
        List<OrderItemDTO> itemDTOs = entity.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getMenuItemId(),
                        item.getMenuItemName(),
                        item.getPriceAtOrder(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                entity.getId(),
                entity.getTableNumber(),
                entity.getAssignedWaiterId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getTotalAmount(),
                itemDTOs
        );
    }

    /**
     * Convert ReservationEntity to ReservationDTO.
     */
    public ReservationDTO toReservationDTO(ReservationEntity entity) {
        CustomerDTO customerDTO = new CustomerDTO(
                entity.getCustomer().getName(),
                entity.getCustomer().getPhone(),
                entity.getCustomer().getEmail()
        );

        return new ReservationDTO(
                entity.getId(),
                customerDTO,
                entity.getReservationTime(),
                entity.getPartySize(),
                entity.getAssignedTable(),
                entity.getStatus()
        );
    }

    /**
     * Convert PaymentEntity to PaymentDTO.
     */
    public PaymentDTO toPaymentDTO(PaymentEntity entity) {
        return new PaymentDTO(
                entity.getTransactionId(),
                entity.getMethod(),
                entity.getAmount(),
                entity.getTimestamp(),
                entity.getOrder() != null ? entity.getOrder().getId() : null
        );
    }

    /**
     * Convert InventoryEntity to InventoryDTO.
     */
    public InventoryDTO toInventoryDTO(InventoryEntity entity) {
        return new InventoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getUnit(),
                entity.getStockLevel(),
                entity.getReorderThreshold(),
                entity.getMaxCapacity(),
                entity.getStatus()
        );
    }
}
