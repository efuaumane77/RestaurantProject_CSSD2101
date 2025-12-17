package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.menu.DietaryType;
import com.university.restaurant.model.menu.MenuCategory;
import jakarta.persistence.*;

/**
 * JPA Entity for MenuItem using SINGLE_TABLE inheritance strategy.
 * All menu item types (Entree, Drink, Dessert, Combo) are stored in one table.
 *
 * @author Mahdis Baradaran
 */
@Entity
@Table(name = "menu_items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class MenuItemEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private MenuCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "dietary_type", nullable = false, length = 20)
    private DietaryType dietaryType;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    // Default constructor for JPA
    protected MenuItemEntity() {}

    // Constructor
    protected MenuItemEntity(String id, String name, String description, Double price,
                             MenuCategory category, DietaryType dietaryType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.available = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public void setCategory(MenuCategory category) {
        this.category = category;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(DietaryType dietaryType) {
        this.dietaryType = dietaryType;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}