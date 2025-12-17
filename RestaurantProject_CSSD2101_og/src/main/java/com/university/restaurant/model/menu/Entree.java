package com.university.restaurant.model.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Efua Itoadon-Umane
 *
 * This represents the course of meal: ENTREE
 * This class is for creating new entree objects to add to the menu. OR for placing orders
 */

public final class Entree extends MenuItem {
    private final List<String> ingredients;
    private final int prepTimeMinutes;

    /**
     *
     * @param id
     * @param name
     * @param description
     * @param price
     * @param dietaryType
     * @param ingredients a list of ingredients required to make the meal
     * @param prepTimeMinutes how long will it take to be ready
     */
    public Entree(String id, String name, String description, double price, DietaryType dietaryType,
           List<String> ingredients, int prepTimeMinutes){
        super(id, name, description, price, MenuCategory.ENTREE, dietaryType);

        this.ingredients = new ArrayList<>(ingredients);
        this.prepTimeMinutes = prepTimeMinutes;
    }
    @Override
    public double calculatePrice(){ return price;}
    @Override
    public boolean requiresKitchenPrep(){ return true;}
    @Override List<String> getRequiredIngredients(){ return List.copyOf(ingredients);}

    public int getPrepTimeMinutes(){ return prepTimeMinutes;}

    @Override
    public MenuItem copyWithPrice(double newPrice) {
        return new Entree(
                this.id,
                this.name,
                this.description,
                newPrice,
                this.dietaryType,
                this.getRequiredIngredients(),
                this.getPrepTimeMinutes()
        );
    }
}

