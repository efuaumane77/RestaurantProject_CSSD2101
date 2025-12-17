package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import jakarta.persistence.*;

/**
 * JPA Entity for Combo menu items.
 * Stored in menu_items table with item_type = 'COMBO'.
 *
 * Note: Combo items reference other menu items, but we store only the
 * discount percentage here. The item IDs are stored in a separate
 * join table (combo_items).
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@DiscriminatorValue("COMBO")
public class ComboEntity extends MenuItemEntity {

    @Column(name = "discount_percent")
    private Double discountPercent;

    @Column(name = "item_ids", length = 500)
    private String itemIds; // Stored as comma-separated item IDs

    // Default constructor for JPA
    public ComboEntity() {
        super();
    }

    // Full constructor
    public ComboEntity(String id, String name, String description,
                       Double discountPercent, String itemIds) {
        super(id, name, description, 0.0, MenuCategory.COMBO, DietaryType.REGULAR);
        this.discountPercent = discountPercent;
        this.itemIds = itemIds;
    }

    // Getters and Setters
    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getItemIds() {
        return itemIds;
    }

    public void setItemIds(String itemIds) {
        this.itemIds = itemIds;
    }
}