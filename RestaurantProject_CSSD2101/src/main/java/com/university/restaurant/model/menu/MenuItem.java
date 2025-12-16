package com.university.restaurant.model.menu;

import java.util.List;

/**
 * @author Efua Itoadon-Umane
 *
 * This is the base class for all meal types to inherit from. 
 * allows you to work with the name, description, price, availability and menu category
 */

public abstract class MenuItem {
    protected final String id, name, description;
    protected final double price;
    protected final MenuCategory category;
    protected final DietaryType dietaryType;
    protected boolean available;

        /**
     * 
     * @param id for the newly added menu item
     * @param name
     * @param description of what the meal is 
     * @param price
     * @param category must choose from the types in the enum 
     * @param dietaryType must choose from the types in the enum 
     */

    protected MenuItem(String id, String name, String description, double price,
                       MenuCategory category, DietaryType dietaryType){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.dietaryType = dietaryType;
        this.available = true;
    }

    public abstract double calculatePrice();
    public abstract boolean requiresKitchenPrep();
    abstract List<String> getRequiredIngredients();

    public abstract MenuItem copyWithPrice(double newPrice);

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "%s[%s: %s | $%.2f | %s]"
                .formatted(getClass().getSimpleName(), id, name, price,
                        available ? "Available" : "Unavailable");
    }
}










