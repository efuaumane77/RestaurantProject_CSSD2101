package com.university.restaurant.infrastructure.adapter.mapper;

import com.university.restaurant.infrastructure.entity.OrderEntity;
import com.university.restaurant.infrastructure.entity.OrderItemEntity;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Order domain models and OrderEntity JPA entities.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Component
public class OrderMapper {

    /**
     * Convert domain Order to JPA entity.
     * Note: This creates a new entity but doesn't handle the full conversion
     * of MenuItems since Order in domain references MenuItem objects.
     */
    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity(
                domain.getId(),
                domain.getTableNumber(),
                domain.getAssignedWaiterId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        entity.setTotalAmount(domain.calculateTotal());

        // Convert order items
        for (MenuItem item : domain.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    item.getId(),
                    item.getName(),
                    item.calculatePrice(),
                    1 // Quantity - domain Order doesn't track quantities per item
            );
            entity.addItem(itemEntity);
        }

        return entity;
    }

    /**
     * Convert JPA entity to domain Order.
     * Note: This is a simplified conversion. In reality, you'd need to
     * reconstruct MenuItem objects from OrderItemEntity data.
     */
    public Order toDomain(OrderEntity entity) {
        // Note: Domain Order constructor requires waiter ID
        Order domain = new Order(
                entity.getTableNumber(),
                entity.getAssignedWaiterId()
        );

        // We can't fully reconstruct the Order's internal state here
        // because the domain Order uses UUID internally
        // This would require reflection or a different approach

        throw new UnsupportedOperationException(
                "Cannot fully convert OrderEntity to Order domain model. " +
                        "Order domain class needs modification to support reconstruction."
        );
    }
}
