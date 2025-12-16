package com.university.restaurant.model.menu;

import java.util.ArrayList;
import java.util.List;

public final class Dessert extends MenuItem {
    private List<String> allergens;

    public Dessert(String id, String name, String description, double price, DietaryType dietaryType,
                   List<String> allergens){
        super(id, name, description, price, MenuCategory.DESSERT, dietaryType);

        this.allergens = new ArrayList<>(allergens);
    }

    @Override
    public double calculatePrice(){ return price;}
    @Override
    public boolean requiresKitchenPrep(){return true;}
    @Override List<String> getRequiredIngredients(){ return List.of(name.toLowerCase());}

    public List<String> getAllergens(){return List.copyOf(allergens);}

    @Override
    public MenuItem copyWithPrice(double newPrice) {
        return new Dessert(
                this.id,
                this.name,
                this.description,
                newPrice,
                this.dietaryType,
                this.getAllergens()
        );
    }
}
