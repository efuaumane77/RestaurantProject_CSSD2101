package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import jakarta.persistence.*;

/**
 * JPA Entity for Dessert menu items.
 * Stored in menu_items table with item_type = 'DESSERT'.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@DiscriminatorValue("DESSERT")
public class DessertEntity extends MenuItemEntity {

    @Column(name = "allergens", length = 500)
    private String allergens; // Stored as comma-separated string

    // Default constructor for JPA
    public DessertEntity() {
        super();
    }

    // Full constructor
    public DessertEntity(String id, String name, String description, Double price,
                         DietaryType dietaryType, String allergens) {
        super(id, name, description, price, MenuCategory.DESSERT, dietaryType);
        this.allergens = allergens;
    }

    // Getters and Setters
    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }
}
