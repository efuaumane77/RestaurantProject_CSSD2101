package com.university.restaurant.infrastructure.adapter.mapper;

import com.university.restaurant.infrastructure.entity.InventoryEntity;
import com.university.restaurant.model.inventory.InventoryItem;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between InventoryItem domain models and InventoryEntity JPA entities.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class InventoryMapper {

    /**
     * Convert domain InventoryItem to JPA entity.
     */
    public InventoryEntity toEntity(InventoryItem domain) {
        return new InventoryEntity(
                domain.getId(),
                domain.getName(),
                domain.getUnit(),
                domain.getStockLevel(),
                domain.getReorderThreshold(),
                domain.getMaxCapacity()
        );
    }

    /**
     * Convert JPA entity to domain InventoryItem.
     */
    public InventoryItem toDomain(InventoryEntity entity) {
        return new InventoryItem(
                entity.getId(),
                entity.getName(),
                entity.getUnit(),
                entity.getStockLevel(),
                entity.getReorderThreshold(),
                entity.getMaxCapacity()
        );
    }
}
