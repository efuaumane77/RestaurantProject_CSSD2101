package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import jakarta.persistence.*;

/**
 * JPA Entity for Entree menu items.
 * Stored in menu_items table with item_type = 'ENTREE'.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@DiscriminatorValue("ENTREE")
public class EntreeEntity extends MenuItemEntity {

    @Column(name = "ingredients", length = 1000)
    private String ingredients; // Stored as comma-separated string

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    // Default constructor for JPA
    public EntreeEntity() {
        super();
    }

    // Full constructor
    public EntreeEntity(String id, String name, String description, Double price,
                        DietaryType dietaryType, String ingredients, Integer prepTimeMinutes) {
        super(id, name, description, price, MenuCategory.ENTREE, dietaryType);
        this.ingredients = ingredients;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    // Getters and Setters
    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }
}