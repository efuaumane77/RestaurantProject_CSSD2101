package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import jakarta.persistence.*;

/**
 * JPA Entity for Drink menu items.
 * Stored in menu_items table with item_type = 'DRINK'.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@DiscriminatorValue("DRINK")
public class DrinkEntity extends MenuItemEntity {

    @Column(name = "is_alcoholic")
    private Boolean isAlcoholic;

    // Default constructor for JPA
    public DrinkEntity() {
        super();
    }

    // Full constructor
    public DrinkEntity(String id, String name, String description, Double price, Boolean isAlcoholic) {
        super(id, name, description, price, MenuCategory.DRINK, DietaryType.REGULAR);
        this.isAlcoholic = isAlcoholic;
    }

    // Getters and Setters
    public Boolean getIsAlcoholic() {
        return isAlcoholic;
    }

    public void setIsAlcoholic(Boolean isAlcoholic) {
        this.isAlcoholic = isAlcoholic;
    }
}