package com.university.restaurant.model.menu;

import java.util.ArrayList;
import java.util.List;

public final class Entree extends MenuItem {
    private final List<String> ingredients;
    private final int prepTimeMinutes;

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